package com.at24.visitors;

import com.at24.CBaseVisitor;
import com.at24.CParser.AssignmentOperatorContext;
import com.at24.CParser.PostfixExpressionContext;
import com.at24.CParser.StorageClassSpecifierContext;
import com.at24.CParser.StructOrUnionContext;
import com.at24.CParser.TypeQualifierContext;
import com.at24.CParser.TypeSpecifierContext;
import com.at24.CParser.UnaryOperatorContext;

public class StringVisitor extends CBaseVisitor<String> {
    @Override
    public String visitUnaryOperator(UnaryOperatorContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitAssignmentOperator(AssignmentOperatorContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitStorageClassSpecifier(StorageClassSpecifierContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitTypeSpecifier(TypeSpecifierContext ctx) {
        if(ctx.atomicTypeSpecifier() != null) {
            return visitAtomicTypeSpecifier(ctx.atomicTypeSpecifier());
        } else if (ctx.structOrUnionSpecifier() != null) {
            return visitStructOrUnionSpecifier(ctx.structOrUnionSpecifier());
        } else if (ctx.enumSpecifier() != null) {
            return visitEnumSpecifier(ctx.enumSpecifier());
        } else if (ctx.typedefName() != null) {
            return visitTypedefName(ctx.typedefName());
        } else {
            return ctx.getText();
        }
    }

    @Override
    public String visitStructOrUnion(StructOrUnionContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitTypeQualifier(TypeQualifierContext ctx) {
        return ctx.getText();
    }
}
