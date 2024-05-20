package com.at24.visitors;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.at24.CBaseVisitor;
import com.at24.CParser;
import com.at24.CParser.DeclarationSpecifierContext;
import com.at24.CParser.InitDeclaratorContext;
import com.at24.exceptions.NotSupportedExpessionException;
import com.at24.translationTools.CompoundData;
import com.at24.translationTools.TypeTranslator;

public class MapVisitor extends CBaseVisitor<CompoundData> {
    public CompoundData visitDeclaration(CParser.DeclarationContext ctx) {
        CompoundData data = new CompoundData();
        data.addData(
            visitDeclarationSpecifiers(ctx.declarationSpecifiers())
        );        

        if(ctx.initDeclaratorList() != null) {
            data.addData(visitInitDeclaratorList(ctx.initDeclaratorList()));
        }

        return data;
    }

    public CompoundData visitInitDeclaratorList(CParser.InitDeclaratorListContext ctx) {
        CompoundData data = new CompoundData();
        List<CompoundData> vars = new LinkedList<>();

        for (InitDeclaratorContext initDeclaratorContext : ctx.initDeclarator()) {
            CompoundData newData = visitInitDeclarator(initDeclaratorContext);
            vars.add(newData);

        }

        data.put("declarations", vars);
        return data;
    }

    public CompoundData visitInitDeclarator(CParser.InitDeclaratorContext ctx) {
        CompoundData data = new CompoundData();
        data.addData(visitDeclarator(ctx.declarator()));
        if(ctx.initializer() != null) {
            data.addData(visitInitializer(ctx.initializer()));
        }
        
        return data;
    }

    public CompoundData visitInitializer(CParser.InitializerContext ctx) {
        if(ctx.assignmentExpression() == null) {
            throw new NotSupportedExpessionException(ctx.getText());
        }

        return visitAssignmentExpression(ctx.assignmentExpression());
    }

    public CompoundData visitAssignmentExpression(CParser.AssignmentExpressionContext ctx) {
        CompoundData data = new CompoundData();
        System.out.println(ctx.DigitSequence());
        System.out.println(ctx.getText());

        if(ctx.DigitSequence() != null) {
            data.put("value", ctx.DigitSequence().getText());
            return data;
        }
        throw new NotSupportedExpessionException(ctx.getText());
    }

    public CompoundData visitDeclarator(CParser.DeclaratorContext ctx) {
        return visitDirectDeclarator(ctx.directDeclarator());
    }

    public CompoundData visitDirectDeclarator(CParser.DirectDeclaratorContext ctx) {
        if(ctx.Identifier() == null) {
            throw new NotSupportedExpessionException(ctx.getText());
        }
        
        CompoundData data = new CompoundData();
        data.put("name", ctx.Identifier().getText());
        return data;
    }

    public CompoundData visitDeclarationSpecifiers(CParser.DeclarationSpecifiersContext ctx) {
        CompoundData data = new CompoundData();

        for (DeclarationSpecifierContext specifierContext : ctx.declarationSpecifier()) {
            data.addData(
                visitDeclarationSpecifier(specifierContext)
            );
        }

        return data;
    }

    public CompoundData visitDeclarationSpecifier(CParser.DeclarationSpecifierContext ctx) {
        CompoundData data = new CompoundData();

        if(ctx.typeSpecifier() == null) {
            throw new NotSupportedExpessionException(ctx.getText());
        }

        data.addData(
            visitTypeSpecifier(ctx.typeSpecifier())  
        );
        return data;
    }

    public CompoundData visitTypeSpecifier(CParser.TypeSpecifierContext ctx) {
        CompoundData data = new CompoundData();
        String text = TypeTranslator.typeConverter(ctx.getText());

        if(text == null) {
            throw new NotSupportedExpessionException(ctx.getText());
        }
        data.put( 
            "rawType",
            text
        );
        return data;
    }
}
