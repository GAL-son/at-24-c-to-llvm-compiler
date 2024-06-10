@echo off
setlocal

if "%1" == "-r" goto run
if "%1" == "--run" goto run
if "%1" == "-l" goto run
if "%1" == "--llvm" goto run
if "%1" == "-b" goto build
if "%1" == "--build" goto build

echo Invalid operation
goto end

:run
set usr=%userprofile%
java -cp "../cllvm/target/classes;%usr%/.m2/repository/org/antlr/antlr4-runtime/4.13.1/antlr4-runtime-4.13.1.jar;%usr%/.m2/repository/org/javatuples/javatuples/1.2/javatuples-1.2.jar;%usr%/.m2/repository/org/json/json/20240303/json-20240303.jar"  com.at24.Main %2 "%3.ll"

if "%1" == "-l" goto end
if "%1" == "--llvm" goto end

clang -o "%3.exe" "%3.ll"
goto end

:build
mvn compile -f ../cllvm/pom.xml
goto end

:end

