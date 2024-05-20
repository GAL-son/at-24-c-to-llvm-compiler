package com.at24;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Main {
    public static void main(String[] args) {
        String code = "int test = 10;";

        CharStream stream = CharStreams.fromString(code);

        CLexer cLexer = new CLexer(stream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(cLexer);

        CParser cParser = new CParser(commonTokenStream);
        CLLVMVisitor cVisitor = new CLLVMVisitor();
        String res = cVisitor.visitCompilationUnit(cParser.compilationUnit());

        System.out.println(res);
    }
}