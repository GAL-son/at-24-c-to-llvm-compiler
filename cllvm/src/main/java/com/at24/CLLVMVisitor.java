package com.at24;

import java.util.List;

import com.at24.CParser.DeclarationSpecifierContext;
import com.at24.CParser.TypeSpecifierContext;
import com.at24.translationTools.*;

public class CLLVMVisitor extends CBaseVisitor<String> {
    
    @Override 
    public String visitDeclaration(CParser.DeclarationContext ctx) { 
        String declarationSpecifier = visitDeclarationSpecifiers(ctx.declarationSpecifiers());
        
        System.out.println(ctx.getText());
        System.out.println(ctx.declarationSpecifiers().getText());
        System.out.println(declarationSpecifier);

        return "";
    }

    @Override 
    public String visitDeclarationSpecifiers(CParser.DeclarationSpecifiersContext ctx) {
        List<DeclarationSpecifierContext> contexts = ctx.declarationSpecifier();

        String resultDeclarationSpecifiers = "";

        for (DeclarationSpecifierContext declarationSpecifierContext : contexts) {
            resultDeclarationSpecifiers = resultDeclarationSpecifiers + visitDeclarationSpecifier(declarationSpecifierContext);
        }

        return resultDeclarationSpecifiers;
    }

    @Override
    public String visitDeclarationSpecifier(CParser.DeclarationSpecifierContext ctx) {
        TypeSpecifierContext typeSpecifierContext = ctx.typeSpecifier();

        if(typeSpecifierContext != null) {
            return visitTypeSpecifier(typeSpecifierContext);
        }
        return "";
    }

    @Override
    public String visitTypeSpecifier(CParser.TypeSpecifierContext ctx) {
        // REGULAR TYPE
        String typeSpecifier = ctx.getText();
        String llvmType = TypeTranslator.typeConverter(typeSpecifier);

        return llvmType;
    }

    
}
