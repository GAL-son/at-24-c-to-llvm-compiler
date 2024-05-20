package com.at24;

public class CLLVMVisitor extends CBaseVisitor<String> {
    
    // @Override 
    // public String visitDeclaration(CParser.DeclarationContext ctx) { 
        

    //     return "";
    // }

    @Override
    public String visitTypeSpecifier(CParser.TypeSpecifierContext ctx) {
        String typeName = ctx.getText();
        String convertedType = "";

        switch (typeName) {
            case char:
                
                break;
        
            default:
                break;
        }

        // System.out.println(typeName);


        return visitChildren(ctx);
    }

    
}
