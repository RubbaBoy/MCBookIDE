# MC Book IDE

Previously, if you wanted to program in Java, you had to have a large application such as IntelliJ, or a much smaller program such as Atom, Notepad++, Notepad, or even Eclipse, if you really wanted lack of features. That was before the creation of MS Paint IDE came into existence, of course, but some people didn't have Windows, and felt left out. Since this is primarily for the Spigot community, most people play Minecraft. So why not have a way for people to program in Minecraft? This is the exact problem MC Book IDE solves, giving you the ability to craft beautiful programs within the game.

_Note: When using this plugin, due to the way Java is designed, if you want to compile classes, you need to run your server off of the JDK, not the JRE._

## Features
Provides full Java syntax highlighting
Highlights in red underlines any compilation errors
Multiple class support with a book per class
In-game execution of programs
Program library support, allowing you to program plugins for your server, on your server
Full packaging of jars from books, with extra files such as manifests, plugin.yml's, etc.

## Commands
The main command with its arguments is:
/<bookcompile|bc|bookc|compilebook|compileb|cb> <highlight|compile|execute>

Arguments:
highlight - This syntax highlights all the signed books in your inventory. Note: Issues may be caused if books have the same name

compile - Syntax highlights the books in your inventory, and compiles them into a jar file

execute - Executes the public static void main(String[] args) method on the first book it finds in your inventory, after syntax highlighting and compiling all the books in your inventory

## Configuration

The configuration allows you to change some settings of how the books are compiled into jars. These are the config paths and their usages, with example values:

compile.jarname - This is the name of the jar that will be compiled from the books. The default value of this is "CompiledJar.jar"

compile.otherfiles - This is the location of other files to be packaged into the jar file. These can be things like Manifests, plugin.yml's, etc. This can be a directory containing files to be packaged, or a singular file. The location is in the plugin's data folder. The default value for this is "OtherFiles" which will put everything in the created folder, into the jar.

compile.classoutput - This is the folder location (In the plugin directory still) to put all of the .class files compiled from books.

compile.libs - The jar file to be used as a library, or a directory containing jars to be libraries. These can be things like Spigot, plugin APIs, etc. The default for this is "libs", so all jars in this folder (In the plugin data folder) will be used for compiling.

## Screenshots

![Executing a book](https://i.imgur.com/KfnwKSa.gif)
![Executing a book with console](https://i.imgur.com/cHCItl5.gif)

An example with code with a missing semicolon:

![Executing book with errors](https://i.imgur.com/8yBmZZM.gif)


Donate
If anyone would like to donate to me to keep the memes flowing, I would be extremely grateful. If you are unable to donate money, throwing a like or a review will help support as well!

Feel free to donate [via PayPal](https://paypal.me/RubbaBoy).
