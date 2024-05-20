package com.at24;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Main {
    public static void main(String[] args) {
        String code = "void main() {int a = 0;}";

        CharStream stream = CharStreams.fromString(code);

        CLexer cLexer = new CLexer(stream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(cLexer);

        CParser cParser = new CParser(commonTokenStream);

        CParser.CompilationUnitContext compilationUnitContext = cParser.compilationUnit();
        CLLVMVisitor cVisitor = new CLLVMVisitor();

        cVisitor.visit(compilationUnitContext);
    }
}