# Usage of the `compile.bat`

You can use provided `compile.bat` script to make building and runing compiler easier.
Script is located in `tools` directory.

> __NOTE:__ Script must be executed from its current location

# Build
To build a project you will require Maven executable to be in PATH system variable.
If that condition is met you can use scriopt with `-b` or `--build` flag.
You can also build project with IDE of your choice as long as it supports maven.

# Running compiler

To use script to compile files you will need to follow syntax:
> ```./compile.bat <run_flag> <input_file.c> [<output_file>]```

Avaliale flags are:
* `-r` and `--run` - Compiles directly to .exe file
* `-l` and `--llvm` - Compiles only to .ll file

## Compile to .exe
To compile `.c` files you can use the script with `-r` or `--run` flag. 
This will compile source file directly to `.exe` file using clang.
> __⚠️IMPORTANT:__ clang must be installed and added to PATH variable

## Compile to .ll
If you can not use clang or wish not to do so, you can still compile to the IR files and use compiler of your choice.
To do this you can execure script with  `-l` or `--llvm` flag. 

## Output file
If output file is not provided, result will be outputed to a **new** file with the same name as input file and extenstions `.ll` and `.exe`