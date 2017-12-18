package com.uddernetworks.mcbook.main;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class CodeCompiler {

    private File classOutputFolder;
    private Map<String, BookClass> imageClassHashMap = new HashMap<>();
    private Map<BookClass, List<Diagnostic<? extends JavaFileObject>>> errors = new HashMap<>();

    public class MyDiagnosticListener implements DiagnosticListener<JavaFileObject> {

        public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
            String packageName = diagnostic.getSource().getName().substring(1).replace("/", ".");
            packageName = packageName.substring(0, packageName.length() - 5);

            System.out.println("packageName = " + packageName);

            BookClass imageClass = imageClassHashMap.get(packageName);

            if (errors.containsKey(imageClass)) {
                errors.get(imageClass).add(diagnostic);
            } else {
                List<Diagnostic<? extends JavaFileObject>> list = new ArrayList<>();
                list.add(diagnostic);
                errors.put(imageClass, list);
            }
        }
    }

    public class InMemoryJavaFileObject extends SimpleJavaFileObject {
        private String contents;

        private InMemoryJavaFileObject(String className, String contents) {
            super(URI.create("string:///" + className.replace('.', '/')
                    + Kind.SOURCE.extension), Kind.SOURCE);
            this.contents = contents;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return contents;
        }
    }

    private JavaFileObject getJavaFileObject(String code, String classPackage, String className) {
        JavaFileObject so = null;
        try {
            so = new InMemoryJavaFileObject(classPackage + "." + className, code);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return so;
    }

    private void compile(Iterable<? extends JavaFileObject> files, List<File> libs) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        MyDiagnosticListener c = new MyDiagnosticListener();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(c, Locale.ENGLISH, null);

        List<String> options = new ArrayList<>(Arrays.asList("-d", classOutputFolder.getAbsolutePath()));

        if (!libs.isEmpty()) {
            options.add("-classpath");

            libs.forEach(lib -> options.add(lib.getAbsolutePath()));
        }

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, c, options, null, files);
        task.call();
    }

    private boolean runIt(String classPackage, String className) {
        try {
            URL url = classOutputFolder.toURL();
            URL[] urls = new URL[]{url};

            ClassLoader loader = new URLClassLoader(urls);

            Class thisClass = loader.loadClass(classPackage + "." + className);

            Object instance = thisClass.newInstance();
            Method thisMethod = thisClass.getDeclaredMethod("main", String[].class);

            if (thisMethod == null) return false;

            System.out.println("Executing main(String[] args) in class " + classPackage + "." + className);

            thisMethod.invoke(instance, new Object[]{new String[0]});

            return true;
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ignored) {}

        return false;
    }

    public Map<BookClass, List<Diagnostic<? extends JavaFileObject>>> compileAndExecute(List<BookClass> imageClasses, File jarFile, File otherFiles, File classOutputFolder, File libsFolder, boolean execute) throws IOException {
        this.classOutputFolder = classOutputFolder;
        classOutputFolder.mkdirs();
        if (otherFiles.isFile()) {
            otherFiles.createNewFile();
        } else {
            otherFiles.mkdirs();
        }

        for (File file : getFilesFromDirectory(classOutputFolder, null)) {
            file.delete();
        }

        long start = System.currentTimeMillis();
        System.out.println("Compiling...");

        List<JavaFileObject> filesList = new ArrayList<>();

        Map<String, String> namePackages = new HashMap<>();

        for (BookClass imageClass : imageClasses) {
            String classPackage = (imageClass.getCode().trim().startsWith("package") ? imageClass.getCode().trim().substring(8, imageClass.getCode().trim().indexOf(";")) : "");
            String[] spaces = imageClass.getCode().trim().split(" ");
            String className = "Main";
            for (int i = 0; i < spaces.length; i++) {
                if (spaces[i].equals("class")) {
                    className = spaces[i + 1];
                    break;
                }
            }

            System.out.println("Class name = " + className);
            System.out.println("Class package = " + classPackage);

            namePackages.put(className, classPackage);
            imageClassHashMap.put(classPackage + "." + className, imageClass);

            filesList.add(getJavaFileObject(imageClass.getCode(), classPackage, className));
        }

        compile(filesList, getFilesFromDirectory(libsFolder, "jar"));

        System.out.println("Compiled in " + String.valueOf((System.currentTimeMillis() - start)) + "ms");

        start = System.currentTimeMillis();
        System.out.println("Packaging jar...");

        if (otherFiles.isDirectory()) {

            copyFolder(otherFiles, classOutputFolder);
        } else {
            File newLoc = new File(classOutputFolder, otherFiles.getName());
            newLoc.createNewFile();
            Files.copy(Paths.get(otherFiles.getAbsolutePath()), Paths.get(newLoc.getAbsolutePath()), REPLACE_EXISTING);
        }

        FileJarrer fileJarrer = new FileJarrer(classOutputFolder, jarFile);
        fileJarrer.jarDirectory();

        System.out.println("Packaged jar in " + (System.currentTimeMillis() - start) + "ms");

        if (!errors.isEmpty()) {
            for (List<Diagnostic<? extends JavaFileObject>> errorList : errors.values()) {
                for (Diagnostic<? extends JavaFileObject> error : errorList) {
                    System.out.println("Error on " + error.getSource().getName() + " [" + error.getLineNumber() + ":" + (error.getColumnNumber() == -1 ? "?" : error.getColumnNumber()) + "] " + error.getMessage(Locale.ENGLISH));
                }
            }
        }

        if (!execute) {
            return errors;
        }

        System.out.println("Executing...");
        start = System.currentTimeMillis();

        for (String className : namePackages.keySet()) {
            if (runIt(namePackages.get(className), className)) break;
        }

        System.out.println("Executed in " + (System.currentTimeMillis() - start) + "ms");

        return errors;
    }


    private static void copyFolder(File src, File dest) throws IOException {
        if (src.isDirectory()) {

            if (!dest.exists()) dest.mkdir();

            for (String file : src.list()) {
                //construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                //recursive copy
                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
        }
    }


    private List<File> getFilesFromDirectory(File directory, String extension) {
        directory.mkdirs();
        List<File> ret = new ArrayList<>();
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                ret.add(file);
                ret.addAll(getFilesFromDirectory(file, extension));
            } else {
                if (extension == null) ret.add(file);
                else if (file.getName().endsWith("." + extension)) ret.add(file);
            }
        }

        return ret;
    }

}
