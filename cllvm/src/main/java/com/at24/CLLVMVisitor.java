package com.at24;

import com.at24.translationTools.*;

public class CLLVMVisitor extends CBaseVisitor<String> {
    
    // @Override 
    // public String visitDeclaration(CParser.DeclarationContext ctx) { 
        

    //     return "";
    // }

    @Override
    public String visitDeclarationSpecifier(CParser.DeclarationSpecifierContext ctx) {
        
    }

    @Override
    public String visitTypeSpecifier(CParser.TypeSpecifierContext ctx) {
        // REGULAR TYPE
        String typeSpecifier = ctx.getText();
        String llvmType = TypeTranslator.translateType(typeSpecifier);

        return llvmType;
    }

    
}
