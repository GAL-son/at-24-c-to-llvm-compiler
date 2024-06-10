# C to LLVM IR Compiler
A simple C to LLVM compiler using ANTLR 4. 

## Usage

To run the compiler after downloading this repo you need too do couple of things first.

1. Install maven - you need maven to build a project
2. Install clang - it will be required to compile `.ll` files into executable
3. Add both maven and clang to your system `PATH` variable
2. run `compile.bat` file with `-b` or `--build`flag

After the project has been built you can run the compiler using `./compile.bat -r <input_file> <output_file>` command

> __NOTE:__ Detailed information about usage of the script can be found [here](.documentation\usage.md)

## Developement configuration
To see Developement configuration go to [Configuration manual](.documentation\config.md)
To work with this repository you'll need Antlr4 JAR file. Localization of this file needs to be added to your CLASSPATH variable.