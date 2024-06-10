package com.at24;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import com.at24.codeBuilding.CodeBuilderVisitor;
import com.at24.files.FileManager;

public class Main {
    public static void main(String[] args) {
        if(args.length == 0) {
            System.err.println("Missing input file");
            return;
        }

        String sourcePath = args[0];

        if(!sourcePath.substring(sourcePath.length()-2).equals(".c")) {
            System.err.println("Invalid source file format! .c expected");
            return;
        }

        String targetPath = null;

        if(args.length < 2) {
            targetPath = sourcePath + ".ll";
        } else {
            targetPath = args[1];
        }
        String code = "";

        try {
            code = FileManager.getFileContents(sourcePath);
        } catch (Exception e) {
            System.err.println("Missing "+ sourcePath + " file!");
            System.err.println(e.getMessage());
            return;
        }

        CharStream stream = CharStreams.fromString(code);
        CLexer cLexer = new CLexer(stream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(cLexer);
        CParser cParser = new CParser(commonTokenStream);
        CodeBuilderVisitor cVisitor = new CodeBuilderVisitor();
        cVisitor.visitCompilationUnit(cParser.compilationUnit());

        try {
            System.out.println(targetPath);
            FileManager.writeToFile(targetPath, cVisitor.getCode(), true);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}