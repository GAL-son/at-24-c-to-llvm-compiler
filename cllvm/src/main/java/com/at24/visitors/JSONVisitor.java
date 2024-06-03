package com.at24.visitors;

import java.util.HashSet;
import java.util.Set;

import com.at24.CParser;
import com.at24.CParser.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.json.JSONArray;
import org.json.JSONObject;

import com.at24.CBaseVisitor;
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
            // System.out.println("nie ejst nullem");
            JSONObject initDeclaratorList = visitInitDeclaratorList(ctx.initDeclaratorList());
            declarationData.put("initDeclaratorList", initDeclaratorList.getJSONArray("initDeclaratorList"));
        }
        else {
            // System.out.println("nie ejst nullem");

        }


        // // // System.out.println(declarationData);

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

        return visitDirectDeclarator(ctx.directDeclarator());

    }

    @Override
    public JSONObject visitDirectDeclarator(DirectDeclaratorContext ctx) {
        JSONObject directDeclarator = new JSONObject();

        if (ctx.Identifier() != null) {
            directDeclarator.put("Identifier", ctx.Identifier().getText());
        } else if (ctx.declarator() != null) {
            directDeclarator.put("Declarator", visitDeclarator(ctx.declarator()));
        } else if (ctx.directDeclarator() != null) {
            directDeclarator.put("directDeclarator", visitDirectDeclarator(ctx.directDeclarator()));
            if (ctx.identifierList() != null) {
                directDeclarator.put("IdentifierList", visitIdentifierList(ctx.identifierList()));
            } else if (ctx.parameterTypeList() != null) {
                directDeclarator.put("parameters", visitParameterTypeList(ctx.parameterTypeList()).getJSONArray("parameters"));
            }
        }


        return directDeclarator;
    }

    //check later
    @Override
    public JSONObject visitParameterList(ParameterListContext ctx) {

        JSONObject parameterList = new JSONObject();
        JSONArray parameters = new JSONArray();
        for (ParameterDeclarationContext pctx : ctx.parameterDeclaration()) {
            JSONObject parameter = visitParameterDeclaration(pctx);
            parameters.put(parameter);
        }
        parameterList.put("parameters", parameters);


        return parameterList;
    }

    @Override
    public JSONObject visitParameterDeclaration(ParameterDeclarationContext ctx) {

        JSONObject Parameter = new JSONObject();

        if (ctx.declarationSpecifiers() != null) {
            // // System.out.println("washere dec");
            Parameter.put("type", visitDeclarationSpecifiers(ctx.declarationSpecifiers()));
        }

        if (ctx.declarator() != null) {
            // // System.out.println("washere dec");
            Parameter.put("identifier", visitDeclarator(ctx.declarator()));
        }
        // // System.out.println("parameter");
        // // System.out.println(Parameter);
        return Parameter;
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
        } else if (ctx.Identifier() != null) {
            primaryExpression.put("identifier", ctx.Identifier().getText());
        } else if (ctx.StringLiteral() != null) {
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
        // // System.out.println("START: " + declarationSpecifiers.toString());
        for (DeclarationSpecifierContext declarationCtx : ctx.declarationSpecifier()) {
            //// System.out.println("jeden!");
            // // System.out.println("ITER===============");
            Set<String> keySet = new HashSet<>(declarationSpecifiers.keySet());


            JSONObject result = visitDeclarationSpecifier(declarationCtx);
            Set<String> resultKeySet = result.keySet();
            // // System.out.println("test final: " + declarationSpecifiers.toString());
            keySet.retainAll(resultKeySet);

            if (!keySet.isEmpty()) {
                throw new RepeatedDeclarationSpecifier(keySet);
            }

            for (String key : resultKeySet) {
                // // System.out.println("PUT " + key + ":" + result.get(key));
                // // System.out.println("before put: " + declarationSpecifiers.toString());
                declarationSpecifiers.put(key, result.get(key));
                // // System.out.println("after put: " + declarationSpecifiers.toString());
            }
        }

        // // System.out.println("final: " + declarationSpecifiers.toString());
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
        JSONObject Support = new JSONObject();
        boolean flag = false;

        for (MultiplicativeExpressionContext multiCtx : ctx.multiplicativeExpression()) {
            JSONObject multiplicative = visitMultiplicativeExpression(multiCtx);
            expressions.put(multiplicative);
            Support = multiplicative;
        }

        for (ParseTree child : ctx.children) {
            if (child.getText().equals("+") || child.getText().equals("-")) {
                operators.put(child.getText());
                flag = true;

            }
        }


        if (!operators.isEmpty()) {
            // // System.out.println("not empty");
            additiveExpr.put("expressions", expressions);
            additiveExpr.put("operators", operators);
        } else {
            additiveExpr = Support;
        }
        return additiveExpr;
    }

    @Override
    public JSONObject visitMultiplicativeExpression(MultiplicativeExpressionContext ctx) {
        JSONObject multiplicativeExpr = new JSONObject();
        JSONArray expressions = new JSONArray();
        JSONArray operators = new JSONArray();
        JSONObject Support = new JSONObject();
        boolean flag = false;

        for (CastExpressionContext castCtx : ctx.castExpression()) {
            JSONObject cast = visitCastExpression(castCtx);
            expressions.put(cast);
            Support = cast;
        }

        for (ParseTree child : ctx.children) {
            String text = child.getText();
            if (text.equals("*") || text.equals("/") || text.equals("%")) {
                operators.put(text);
                flag = true;
            }
        }


        if (!operators.isEmpty()) {
            multiplicativeExpr.put("expressions", expressions);
            multiplicativeExpr.put("operators", operators);
        } else {
            multiplicativeExpr = (Support);
        }
        // // System.out.println("end");
        return multiplicativeExpr;
    }

    @Override
    public JSONObject visitFunctionDefinition(FunctionDefinitionContext ctx) {

        JSONObject functionDefinition = new JSONObject();

        if (ctx.declarationSpecifiers() != null) {
            JSONObject declarationSpecifiers = new JSONObject();
            declarationSpecifiers = visitDeclarationSpecifiers(ctx.declarationSpecifiers());
            functionDefinition.put("declarationSpecifiers", declarationSpecifiers);
            // // System.out.println("fdef");
        }
        if (ctx.declarator() != null) {
            JSONObject declarator = new JSONObject();
            declarator = visitDeclarator(ctx.declarator());
            functionDefinition.put("declarator", declarator);
            // // System.out.println("fdec");
        }
        if (ctx.compoundStatement() != null) {
            JSONObject compoundStatement = new JSONObject();
            compoundStatement = visitCompoundStatement(ctx.compoundStatement());
            functionDefinition.put("compoundStatement", compoundStatement);
            // // System.out.println("fcst");
        }

        // // System.out.println(functionDefinition);
        return functionDefinition;
    }


    @Override
    public JSONObject visitCompoundStatement(CompoundStatementContext ctx) {
        JSONObject codeBlock = new JSONObject();

        if (ctx.blockItemList() != null) {
            codeBlock = visitBlockItemList(ctx.blockItemList());

        }

        return codeBlock;
    }

    @Override
    public JSONObject visitBlockItemList(BlockItemListContext ctx) {
        JSONObject codeBlock = new JSONObject();
        JSONArray codeItems = new JSONArray();

        for (BlockItemContext bctx : ctx.blockItem()) {
            JSONObject Bitem = visitBlockItem(bctx);
            codeItems.put(Bitem);
        }


        return codeBlock.put("codeItems", codeItems);
    }

    @Override
    public JSONObject visitJumpStatement(JumpStatementContext ctx) {
        JSONObject jumpStatement = new JSONObject();

        // very unfinished
        // System.out.println("jumps");

        if (ctx.Return() != null) {
            jumpStatement.put("jump", ctx.Return().getText());
            if (ctx.expression() != null) {
                jumpStatement.put("expression", visitExpression(ctx.expression()));
            }
        }

        return jumpStatement;
    }

    @Override
    public JSONObject visitPostfixExpression(PostfixExpressionContext ctx) {
        JSONObject funcCall = new JSONObject();
      //  // System.out.println("that happened1");

        if (ctx.primaryExpression() != null) {
           // // System.out.println("that happened2");
            // System.out.println(ctx.getText());
            if(ctx.getText().contains("(")&&ctx.argumentExpressionList().size()==0)
            {
                funcCall.put("name", visitPrimaryExpression(ctx.primaryExpression()).get("identifier"));

                //// System.out.println("that happened3");

                funcCall.put("arguments",new JSONArray());
            }else
            if (ctx.argumentExpressionList().size() != 0) {
                funcCall.put("name", visitPrimaryExpression(ctx.primaryExpression()).get("identifier"));


                    for (ArgumentExpressionListContext actx : ctx.argumentExpressionList()) {
                        funcCall.put("arguments", visitArgumentExpressionList(actx).get("arguments"));
                    }

                //// System.out.println(funcCall);
            } else {
                return visitPrimaryExpression(ctx.primaryExpression());
            }


        }


        return funcCall;
    }

    @Override
    public JSONObject visitArgumentExpressionList(ArgumentExpressionListContext ctx) {
        JSONObject argument = new JSONObject();
        JSONArray arguments = new JSONArray();

        for (AssignmentExpressionContext actx : ctx.assignmentExpression()) {
            arguments.put(visitAssignmentExpression(actx));
        }

        argument.put("arguments", arguments);
        return argument;
    }

//    @Override
//    public JSONObject visitSelectionStatement(SelectionStatementContext ctx)
//    {
//        JSONObject selectionStatement=new JSONObject();
//
//        if(ctx.if)
//
//        return selectionStatement;
//    }
@Override
public JSONObject visitLogicalOrExpression(LogicalOrExpressionContext ctx) {
    JSONObject orExpression = new JSONObject();
    JSONArray expressions=new JSONArray();


    //// System.out.println("wejscie test or");

    if (ctx.children.size()==1)
    {
        //// System.out.println("test or stopped");
        return visitLogicalAndExpression(ctx.logicalAndExpression(0));
    }

    for (LogicalAndExpressionContext aCtx : ctx.logicalAndExpression()) {
        expressions.put(visitLogicalAndExpression(aCtx));
    }
    orExpression.put("expressions",expressions);
    //// System.out.println("test or"+orExpression);

    return orExpression;
}

    @Override
    public JSONObject visitLogicalAndExpression(LogicalAndExpressionContext ctx) {
        JSONObject andExpression = new JSONObject();
        JSONArray expressions=new JSONArray();


       // // System.out.println("wejscie test and");

        if (ctx.children.size()==1)
        {
           // // System.out.println("test and stopped");
            return visitInclusiveOrExpression(ctx.inclusiveOrExpression(0));
        }

        for (InclusiveOrExpressionContext iCtx : ctx.inclusiveOrExpression()) {
            expressions.put(visitInclusiveOrExpression(iCtx));
        }
        andExpression.put("expressions",expressions);
       // // System.out.println("test and"+andExpression);

        return andExpression;
    }

    @Override
    public JSONObject visitEqualityExpression(EqualityExpressionContext ctx) {
        JSONObject equalityExpr = new JSONObject();
        JSONArray expressions = new JSONArray();
        JSONArray operators = new JSONArray();

        if (ctx.children.size() == 1)
            return visitRelationalExpression(ctx.relationalExpression(0));

        for (RelationalExpressionContext relCtx : ctx.relationalExpression()) {
            expressions.put(visitRelationalExpression(relCtx));
        }

        for (int i = 1; i < ctx.getChildCount(); i += 2) {
            String operator = ctx.getChild(i).getText();
            operators.put(operator);
        }


        equalityExpr.put("expressions", expressions);
        equalityExpr.put("operators", operators);


        return equalityExpr;
    }

    @Override
    public JSONObject visitRelationalExpression(RelationalExpressionContext ctx) {
        JSONObject relationalExpr = new JSONObject();
        JSONArray expressions = new JSONArray();
        JSONArray operators = new JSONArray();

        if (ctx.children.size() == 1)
            return visitShiftExpression(ctx.shiftExpression(0));

        for (ShiftExpressionContext sCtx : ctx.shiftExpression()) {
            expressions.put(visitShiftExpression(sCtx));
        }

        for (int i = 1; i < ctx.getChildCount(); i += 2) {
            String operator = ctx.getChild(i).getText();
            operators.put(operator);
        }


        relationalExpr.put("expressions", expressions);
        relationalExpr.put("operators", operators);


        return relationalExpr;
    }

    @Override
    public JSONObject visitAssignmentExpression(AssignmentExpressionContext ctx)
    {
        JSONObject assignmentExpression=new JSONObject();
        if (ctx.children.size()==1)
        {
            return visitConditionalExpression(ctx.conditionalExpression());
        }
        if(ctx.unaryExpression()!=null)
        {
            assignmentExpression.put("identifier",visitUnaryExpression(ctx.unaryExpression()).get("identifier"));
        }
        if (ctx.assignmentExpression()!=null)
        {
            assignmentExpression.put("expression",visitAssignmentExpression(ctx.assignmentExpression()));
        }

        return assignmentExpression;
    }


}


 