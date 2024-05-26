package com.at24.visitors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import com.at24.CBaseVisitor;
import com.at24.CParser;
import com.at24.CParser.DeclarationContext;
import com.at24.CParser.DeclarationSpecifierContext;
import com.at24.CParser.DeclarationSpecifiersContext;
import com.at24.CParser.TypeSpecifierContext;
import com.at24.exceptions.NotSupportedExpessionException;
import com.at24.translationTools.*;

public class CLLVMVisitor extends CBaseVisitor<String> {

    // public Map<String, DeclarationData> globalVariables = new HashMap<>();
    
    // @Override 
    // public String visitDeclaration(CParser.DeclarationContext ctx) { 
    //     System.out.println("visitDeclaration: " + ctx.getText());

    //     // staticAssertDeclaration NOT SUPPORTED
    //     if(ctx.staticAssertDeclaration() != null) {
    //         throw new NotSupportedExpessionException(ctx.staticAssertDeclaration().getText());
    //     }

    //     String result = "";
    //     MapVisitor visitor = new MapVisitor();
    //     CompoundData declarationData = visitor.visitDeclaration(ctx);

    //     boolean isGlobal = true;
    //     String type = (String)declarationData.get("rawType");
    //     List<CompoundData> vars = (List<CompoundData>)declarationData.get("declarations");
    //     for (CompoundData var : vars) {
    //         if(isGlobal) {
    //             result += VarBuilder.buildGlobal((String)var.get("name"), type, (String)var.get("value"));
    //         }
    //     }
    //     // System.out.println(type);



    //     // // declarationSpecifiers initDeclaratorList? ';'
    //     // DeclarationVisitor decVis = new DeclarationVisitor();
    //     // DeclarationData data = decVis.visitDeclaration(ctx);


    //     // if(data.isFunction) {
    //     //     // Define Function
    //     //     // Ignore 
    //     // }

    //     // else {
    //     //     // Define Variable
    //     //     boolean isGlobal = true;
    //     //     if(isGlobal) {
    //     //         for (Pair<String, String> var : data.namesAndValues) {
    //     //             result += VarBuilder.buildGlobal(var.getValue0(), data.type, var.getValue1());
    //     //         }
    //     //     }
    //     // }
    //     System.out.println(result);

    //     return result;
    // }

    // @Override 
    // public String visitDeclarationSpecifiers(CParser.DeclarationSpecifiersContext ctx) {
    //     List<DeclarationSpecifierContext> contexts = ctx.declarationSpecifier();

    //     String resultDeclarationSpecifiers = "";

    //     for (DeclarationSpecifierContext declarationSpecifierContext : contexts) {
    //         resultDeclarationSpecifiers = resultDeclarationSpecifiers + visitDeclarationSpecifier(declarationSpecifierContext) + " ";
    //     }

    //     return resultDeclarationSpecifiers;
    // }

    // @Override
    // public String visitDeclarationSpecifier(CParser.DeclarationSpecifierContext ctx) {
    //     TypeSpecifierContext typeSpecifierContext = ctx.typeSpecifier();

    //     if(typeSpecifierContext != null) {
    //         return visitTypeSpecifier(typeSpecifierContext);
    //     }
    //     return "";
    // }

    // @Override
    // public String visitTypeSpecifier(CParser.TypeSpecifierContext ctx) {
    //     // REGULAR TYPE
    //     String typeSpecifier = ctx.getText();
    //     String llvmType = TypeTranslator.typeConverter(typeSpecifier);

    //     return llvmType;
    // }

    
}
