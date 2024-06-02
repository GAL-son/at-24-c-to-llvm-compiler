package com.at24;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import com.at24.codeBuilding.CodeBuilderVisitor;

public class Main {
    public static void main(String[] args) {
        String code = "int main(){int a=1;int b=3;if(2!=2||1==1&&1==1){b=1;}};";

        CharStream stream = CharStreams.fromString(code);

        CLexer cLexer = new CLexer(stream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(cLexer);

        CParser cParser = new CParser(commonTokenStream);
        CodeBuilderVisitor cVisitor = new CodeBuilderVisitor();
        cVisitor.visitCompilationUnit(cParser.compilationUnit());

        System.out.println(cVisitor.getCode());
    }
}