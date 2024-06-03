package com.at24;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import com.at24.codeBuilding.CodeBuilderVisitor;

public class Main {
    public static void main(String[] args) {
        String code = "int fibonacci(int limit){int a = 0; b = 1; int result = 0; if(limit == 0) {result = a} else {int counter = 0; while(counter < limit){result = a + b; a = b; b = result}} return result;} void loop() {while(1){while(1){}}";

        CharStream stream = CharStreams.fromString(code);

        CLexer cLexer = new CLexer(stream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(cLexer);

        CParser cParser = new CParser(commonTokenStream);
        CodeBuilderVisitor cVisitor = new CodeBuilderVisitor();
        cVisitor.visitCompilationUnit(cParser.compilationUnit());

        System.out.println(cVisitor.getCode());
    }
}