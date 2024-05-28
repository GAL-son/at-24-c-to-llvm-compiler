package com.at24;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import com.at24.visitors.CLLVMVisitor;
import com.at24.visitors.JSONVisitor;

public class Main {
    public static void main(String[] args) {
        String code = "const int test = 10;int a = 5; int c=1*5+8;";

        CharStream stream = CharStreams.fromString(code);

        CLexer cLexer = new CLexer(stream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(cLexer);

        CParser cParser = new CParser(commonTokenStream);
        JSONVisitor cVisitor = new JSONVisitor();
        cVisitor.visitCompilationUnit(cParser.compilationUnit());

        // System.out.println(res);
    }
}