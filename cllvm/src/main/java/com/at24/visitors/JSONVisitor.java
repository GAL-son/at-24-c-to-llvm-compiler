package com.at24.visitors;

import java.util.HashSet;
import java.util.Set;

import com.at24.CParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.json.JSONArray;
import org.json.JSONObject;

import com.at24.CBaseVisitor;
import com.at24.CParser.DeclarationContext;
import com.at24.CParser.DeclarationSpecifierContext;
import com.at24.CParser.DeclarationSpecifiersContext;
import com.at24.CParser.DeclaratorContext;
import com.at24.CParser.DirectDeclaratorContext;
import com.at24.CParser.InitDeclaratorContext;
import com.at24.CParser.InitDeclaratorListContext;
import com.at24.CParser.InitializerContext;
import com.at24.CParser.PrimaryExpressionContext;
import com.at24.CParser.StructOrUnionSpecifierContext;
import com.at24.CParser.MultiplicativeExpressionContext;
import com.at24.CParser.AdditiveExpressionContext;
import com.at24.CParser.CastExpressionContext;
import com.at24.exceptions.RepeatedDeclarationSpecifier;

import javax.swing.tree.TreeNode;


public class JSONVisitor extends CBaseVisitor<JSONObject> {

    /**
     * Data:
     * <ul>
     * <li>declarationspecifiers</li>
     * <li>initDeclaratorList?</li>
     * </ul>
     * </p>
     * or {@link #visitStaticAssertDeclaration()}
     */
    @Override
    public JSONObject visitDeclaration(DeclarationContext ctx) {
        // Pass result from single rule
        if (ctx.staticAssertDeclaration() != null) {
            return visitStaticAssertDeclaration(ctx.staticAssertDeclaration());
        }

        // Agregate result
        JSONObject declarationData = new JSONObject();

        // Declaration Specifiers
        declarationData.put("declarationSpecifiers", visitDeclarationSpecifiers(ctx.declarationSpecifiers()));

        // Init Declarator List
        if (ctx.initDeclaratorList() != null) {
            JSONObject initDeclaratorList = visitInitDeclaratorList(ctx.initDeclaratorList());
            declarationData.put("initDeclaratorList", initDeclaratorList.getJSONArray("initDeclaratorList"));
        }

        System.out.println(declarationData);

        return declarationData;
    }

    @Override
    public JSONObject visitInitDeclaratorList(InitDeclaratorListContext ctx) {
        JSONObject result = new JSONObject();
        JSONArray declarators = new JSONArray();

        for (InitDeclaratorContext declaratorCtx : ctx.initDeclarator()) {
            JSONObject declaratorResult = visitInitDeclarator(declaratorCtx);
            declarators.put(declaratorResult);
        }

        result.put("initDeclaratorList", declarators);
        return result;
    }

    @Override
    public JSONObject visitInitDeclarator(InitDeclaratorContext ctx) {
        JSONObject initDeclarator = new JSONObject();

        JSONObject declarator = visitDeclarator(ctx.declarator());
        initDeclarator.put("declarator", declarator);

        if (ctx.initializer() != null) {
            JSONObject initializer = visitInitializer(ctx.initializer());
            initDeclarator.put("initializer", initializer);
        }

        return initDeclarator;
    }

    @Override
    public JSONObject visitDeclarator(DeclaratorContext ctx) {
        JSONObject declarator = new JSONObject();

        JSONObject directDeclarator = visitDirectDeclarator(ctx.directDeclarator());
        declarator.put("directDeclarator", directDeclarator);


        return declarator;
    }

    @Override
    public JSONObject visitDirectDeclarator(DirectDeclaratorContext ctx) {
        JSONObject directDeclarator = new JSONObject();

        if (ctx.Identifier() != null) {
            directDeclarator.put("Identifier", ctx.Identifier().getText());
        } else if (ctx.declarator() != null) {
            return visitDeclarator(ctx.declarator());
        }

        return directDeclarator;
    }

    @Override
    public JSONObject visitInitializer(InitializerContext ctx) {
        JSONObject initializer = null;
        ;

        if (ctx.assignmentExpression() != null) {
            return visitAssignmentExpression(ctx.assignmentExpression());
        }

        return initializer;
    }

    @Override
    public JSONObject visitPrimaryExpression(PrimaryExpressionContext ctx) {
        JSONObject primaryExpression = new JSONObject();

        if (ctx.Constant() != null) {
            primaryExpression.put("Constant", ctx.Constant().getText());
        }  else if (ctx.Identifier() != null) {
            primaryExpression.put("identifier", ctx.Identifier().getText());
        }else if (ctx.StringLiteral() != null) {
        }

        return primaryExpression;
    }

    /**
     * Data:
     * storageClassSpecifier
     * typeSpecifier
     * typeQualifier
     * functionSpecifier
     * alignmentSpecifier
     */
    @Override
    public JSONObject visitDeclarationSpecifiers(DeclarationSpecifiersContext ctx) {
        JSONObject declarationSpecifiers = new JSONObject();
        System.out.println("START: " + declarationSpecifiers.toString());
        for (DeclarationSpecifierContext declarationCtx : ctx.declarationSpecifier()) {
            System.out.println("ITER===============");
            Set<String> keySet = new HashSet<>(declarationSpecifiers.keySet());


            JSONObject result = visitDeclarationSpecifier(declarationCtx);
            Set<String> resultKeySet = result.keySet();
            System.out.println("test final: " + declarationSpecifiers.toString());
            keySet.retainAll(resultKeySet);

            if (!keySet.isEmpty()) {
                throw new RepeatedDeclarationSpecifier(keySet);
            }

            for (String key : resultKeySet) {
                System.out.println("PUT " + key + ":" + result.get(key));
                System.out.println("before put: " + declarationSpecifiers.toString());
                declarationSpecifiers.put(key, result.get(key));
                System.out.println("after put: " + declarationSpecifiers.toString());
            }
        }

        System.out.println("final: " + declarationSpecifiers.toString());
        return declarationSpecifiers;
    }

    @Override
    public JSONObject visitDeclarationSpecifier(DeclarationSpecifierContext ctx) {
        JSONObject result = new JSONObject();
        StringVisitor visitor = new StringVisitor();
        if (ctx.typeSpecifier() != null) {
            String res = visitor.visitTypeSpecifier(ctx.typeSpecifier());
            result.put("typeSpecifier", res);
        } else if (ctx.typeQualifier() != null) {
            String res = visitor.visitTypeQualifier(ctx.typeQualifier());
            result.put("typeQualifier", res);
        }

        return result;
    }


    @Override
    public JSONObject visitStructOrUnionSpecifier(StructOrUnionSpecifierContext ctx) {
        JSONObject result = new JSONObject();
        StringVisitor stringVisitor = new StringVisitor();
        result.put("structOrUnion", stringVisitor.visitStructOrUnion(ctx.structOrUnion()));
        result.put("Identifier", ctx.Identifier().getText());
        result.put("structDeclarationList", visitStructDeclarationList(ctx.structDeclarationList()));

        return result;
    }

    @Override
    public JSONObject visitAdditiveExpression(AdditiveExpressionContext ctx) {
        JSONObject additiveExpr = new JSONObject();
        JSONArray expressions = new JSONArray();
        JSONArray operators = new JSONArray();
        JSONObject Support= new JSONObject();
        boolean flag = false;

        for (MultiplicativeExpressionContext multiCtx : ctx.multiplicativeExpression()) {
            JSONObject multiplicative = visitMultiplicativeExpression(multiCtx);
            expressions.put(multiplicative);
            Support=multiplicative;
        }

        for (ParseTree child : ctx.children) {
            if (child.getText().equals("+") || child.getText().equals("-")) {
                operators.put(child.getText());
                flag=true;

            }
        }


        if (!operators.isEmpty()) {
            System.out.println("not empty");
            additiveExpr.put("expressions", expressions);
            additiveExpr.put("operators", operators);
        }
        else{
            additiveExpr=Support;
        }
        return additiveExpr;
    }

    @Override
    public JSONObject visitMultiplicativeExpression(MultiplicativeExpressionContext ctx) {
        JSONObject multiplicativeExpr = new JSONObject();
        JSONArray expressions = new JSONArray();
        JSONArray operators = new JSONArray();
        JSONObject Support= new JSONObject();
        boolean flag = false;

        for (CastExpressionContext castCtx : ctx.castExpression()) {
            JSONObject cast = visitCastExpression(castCtx);
            expressions.put(cast);
            Support=cast;
        }

        for (ParseTree child : ctx.children) {
            String text = child.getText();
            if (text.equals("*") || text.equals("/") || text.equals("%")) {
                operators.put(text);
                flag=true;
            }
        }



        if (!operators.isEmpty()) {
            multiplicativeExpr.put("expressions", expressions);
            multiplicativeExpr.put("operators", operators);
        }
        else {
            multiplicativeExpr=(Support);
        }
        System.out.println("end");
        return multiplicativeExpr;
    }
}


 