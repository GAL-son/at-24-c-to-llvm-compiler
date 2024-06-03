package com.at24.codeBuilding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.antlr.v4.runtime.ParserRuleContext;
import org.json.JSONArray;
import org.json.JSONObject;

import com.at24.CBaseVisitor;
import com.at24.CParser;
import com.at24.CParser.AssignmentExpressionContext;
import com.at24.CParser.CompilationUnitContext;
import com.at24.CParser.DeclarationContext;
import com.at24.CParser.ExpressionContext;
import com.at24.CParser.FunctionDefinitionContext;
import com.at24.CParser.IterationStatementContext;
import com.at24.CParser.JumpStatementContext;
import com.at24.CParser.PostfixExpressionContext;
import com.at24.CParser.SelectionStatementContext;
import com.at24.codeBuilding.codeFeatures.CodeContext;
import com.at24.codeBuilding.codeFeatures.Expression;
import com.at24.codeBuilding.codeFeatures.functions.Function;
import com.at24.codeBuilding.codeFeatures.variables.Variable;
import com.at24.codeBuilding.codeFeatures.variables.VariableTreeReader;
import com.at24.exceptions.DoubleDeclarationException;
import com.at24.visitors.JSONVisitor;

public class CodeBuilderVisitor extends CBaseVisitor<String> implements CodeContext {
    String code;
    Map<String, Variable> vars;
    Map<String, Function> funcs;
    Map<Expression, String> regs;
    CodeBuilderVisitor parent = null;
    Function currentFunction = null;
    Stack<String> breakLabels = new Stack<>();

    JSONVisitor jsonVisitor;

    int labelCounter = 1;
    int registerNumber = 1;

    public CodeBuilderVisitor() {
        jsonVisitor = new JSONVisitor();
        code = "";
        vars = new HashMap<>();
        funcs = new HashMap<>();
        regs = new HashMap<>();
    }

    public CodeBuilderVisitor(CodeBuilderVisitor parent) {
        this();
        this.parent = parent;
        registerNumber = parent.registerNumber;
    }

    public CodeBuilderVisitor(CodeBuilderVisitor parent, Function func) {
        this(parent);
        currentFunction = func;
    }

    public String getCode() {
        return code;
    }

    @Override
    public void emit(String emit) {
        if(!isGlobal()) {
            parent.emit("\t"+emit);
            return;
        }
        code += emit + "\n";
    }

    @Override
    public void emit(String code, String preLine) {
        emit(preLine + code);
        
    }

    @Override
    public void emitOnTop(String emit) {
        if(!isGlobal()) {
            parent.emitOnTop(emit);
            return;
        }
        code = emit + "\n" + code;
        
    }

    @Override
    public Variable searchVariable(String variableName) {
        if(currentFunction != null && currentFunction.hasParam(variableName)) {
            return currentFunction.getParamVariable(variableName);
        }

        // Search variable in current context
        if (vars.containsKey(variableName)) {
            return vars.get(variableName);
        } else if (isGlobal()) { 
            // At root - Var does not exist
            return null;
        } else {
            // Search in parent context
            return parent.searchVariable(variableName);
        }
    }

    @Override
    public Function searchFunction(String functionName) {
        // Go to root context
        if (!isGlobal()) {
            return parent.searchFunction(functionName);
        }

        // In root context search for Function names
        if(funcs.containsKey(functionName)) {
            return funcs.get(functionName);
        } else {
            return null;
        }
    }

    @Override
    public boolean isGlobal() {
        return parent == null;
    }

    @Override
    public String getRegisterName(Expression expression) {
        if(regs.containsKey(expression)) {
            return regs.get(expression);
        } else if (parent != null) {
            return parent.getRegisterName(expression);
        } else {
            return null;
        }
    }

    @Override
    public String borrowRegister() {
        if(getCurrentFunction().equals(parent.getCurrentFunction())) {
            return parent.borrowRegister();
        }
        int ret = registerNumber++;
        return Integer.toString(ret);

    }

    @Override
    public String assignRegister(Expression expression) {
        regs.put(expression, borrowRegister());
        return regs.get(expression);
    }

    private void declareVariable(JSONObject variableDeclaration) {
        Variable var = new Variable(variableDeclaration);

        // If current context has double declaration
        if (searchVariable(var.identifier) != null) {
            throw new DoubleDeclarationException();
        }


        vars.put(var.identifier, var);
        var.parse(this);
    }

    @Override
    public String visitCompilationUnit(CompilationUnitContext arg0) {
        super.visitCompilationUnit(arg0);

        for (var entry : funcs.entrySet()) {
            Function f = entry.getValue();

            if(!f.isDefined()) {
                // Declare outside function
                f.parse(this);
            }
        }

        return code;
    }

    private void declareFunction(JSONObject functionDeclaration) {
        // // System.out.println("functionDeclaration " + functionDeclaration);
        Function func = new Function(functionDeclaration, false);
        funcs.put(func.getIdentifier(), func);
    }

    public boolean variableExists(String var) {
        if (currentFunction.hasParam(var)) {
            return true;
        }

        if (!vars.containsKey(var)) {
            if (parent == null) {
                return false;
            } else {
                return parent.variableExists(var);
            }
        }

        return true;
    }

    private void handleReturn(JSONObject jumpStatement) {
        if(currentFunction == null) {
            throw new RuntimeException("Return statement outside function body");
        }

        // System.out.println("DO RETURN STUFF");
        currentFunction.buildReturn(this, jumpStatement.getJSONObject("expression"));
    }

    private int getLine(ParserRuleContext ctx) {
        return ctx.getStart().getLine();
    }

    public boolean isVariableFunctionArg(String varName) {
        return currentFunction.hasParam(varName);
    }

    @Override
    public String visitDeclaration(DeclarationContext ctx) {
        JSONObject declaration = new JSONVisitor().visitDeclaration(ctx);;
        if (VariableTreeReader.isDeclarationVariable(declaration)) {
            // Do variable declaration
            try {
                declareVariable(declaration);
            } catch (RuntimeException e) {
                throw new RuntimeException(e.getMessage() + " At line: " + getLine(ctx), e);
            }
        } else { // Check for function declaration
            try {
                declareFunction(declaration);
            } catch (RuntimeException e) {
                throw new RuntimeException(e.getMessage() + " At line: " + getLine(ctx), e);
            }
        }

        return code;
    }

    @Override
    public String visitAssignmentExpression(AssignmentExpressionContext ctx) {
        if(ctx.conditionalExpression()!= null) {
            return visitConditionalExpression(ctx.conditionalExpression());
        }
        
        JSONObject assignData = jsonVisitor.visit(ctx);
        // TODO Auto-generated method stub
        String varName = assignData.getString("identifier");
        // System.out.println(assignData);

        Variable var = searchVariable(varName);
        if(var == null) {
            throw new RuntimeException("Usage of undefined variable");
        }

        var.storeToVariable(this, new Expression(assignData.getJSONObject("expression")));

        return code;
    }

    @Override
    public String visitFunctionDefinition(FunctionDefinitionContext ctx) {
        JSONVisitor visitor = new JSONVisitor();
        JSONObject funcData = new JSONObject();
        funcData.put(
            "declarationSpecifiers",
            visitor.visitDeclarationSpecifiers(ctx.declarationSpecifiers())
        );
        funcData.put(
            "declarator",
            visitor.visitDeclarator(ctx.declarator())
        );

        Function func = new Function(funcData, true);
        funcs.put(func.getIdentifier(), func);
        func.define();
        func.parse(this);
                
        CodeBuilderVisitor newContext = new CodeBuilderVisitor(this, func);

        newContext.visitCompoundStatement(ctx.compoundStatement());

        func.endDeclarationParse(this);
        return code;
    }

    @Override
    public String visitJumpStatement(JumpStatementContext ctx) {
        JSONVisitor visitor = new JSONVisitor();
        JSONObject jumpStatement = visitor.visitJumpStatement(ctx);

        String jump = jumpStatement.getString("jump");

        if(jump.equals("return")) {
            // Do return stuff
            handleReturn(jumpStatement);
        }
        return super.visitJumpStatement(ctx);
    }

    @Override
    public String visitEqualityExpression(CParser.EqualityExpressionContext ctx){

        JSONVisitor visitor = new JSONVisitor();

        JSONObject equalityExpression = visitor.visitEqualityExpression(ctx);
        return super.visitEqualityExpression(ctx);
    }
    @Override
    public String visitLogicalOrExpression(CParser.LogicalOrExpressionContext ctx){

        JSONVisitor visitor = new JSONVisitor();

        JSONObject logicalOrExpression = visitor.visitLogicalOrExpression(ctx);
        return super.visitLogicalOrExpression(ctx);
    }

    @Override
    public String visitLogicalAndExpression(CParser.LogicalAndExpressionContext ctx){

        JSONVisitor visitor = new JSONVisitor();

        JSONObject logicalAndExpression = visitor.visitLogicalAndExpression(ctx);
        return super.visitLogicalAndExpression(ctx);
    }


    @Override
    public String visitPostfixExpression(PostfixExpressionContext ctx) {
        JSONVisitor visitor = new JSONVisitor();
        JSONObject call = visitor.visitPostfixExpression(ctx);
        
        if(call.has("arguments")) {
            // Call function
            Function func = searchFunction(call.getString("name"));
            if(func != null) {
                JSONArray args = call.getJSONArray("arguments");
                List<String> argsRegs = new ArrayList<>(args.length());
                for (int i = 0; i < args.length(); i++) {
                    Expression expr = new Expression(args.getJSONObject(i));
                    expr.parse(this);
                    argsRegs.add(expr.getExprIdentifier(this));
                }
                emit(func.callFunction(this, argsRegs));
            }
        }

        return super.visitPostfixExpression(ctx);
    }
    
    @Override
    public String visitSelectionStatement(SelectionStatementContext ctx) {
        JSONVisitor visitor = new JSONVisitor();

        JSONObject expresion = visitor.visitExpression(ctx.expression());
        Expression ifExpresion = new Expression(expresion);
        String exprRegister = "";

        // System.out.println("Selection");
        if(ctx.getText().contains("switch")) {
            // switch
        } else {
            String expType = ifExpresion.getType(this);
            // System.out.println("EXPR TYPE " + expType);

            if(!expType.equals("bool")) {
                // Cast to bool
                Expression boolExpresion = Expression.castTo(ifExpresion, "bool");
                boolExpresion.parse(this);
                expType = boolExpresion.getType(this);
                exprRegister = boolExpresion.getExprIdentifier(this);
            }
            else {
                ifExpresion.parse(this);
                exprRegister = ifExpresion.getExprIdentifier(this);
                expType = ifExpresion.getType(this);
            }
            
            String labelId = getLabel();
            String ifTrueLabel = String.join("", "label.",labelId,".ifTrue");
            String ifFalseLabel = String.join("", "label.",labelId,".ifFalse");
            String endLabel = String.join("", "label.",labelId,".ifend");

            String codeGoToEnd = String.join(" ", "br label", "%"+endLabel);

            String ifCode = String.join(" ", 
                "br",
                CodeTranslator.typeConverter(expType),
                ifExpresion.getExprIdentifier(this)+",",
                "label",
                "%"+ifTrueLabel + ",",
                "label",
                "%"+ifFalseLabel
            );

            emit(ifCode);
            CodeBuilderVisitor cbv = new CodeBuilderVisitor(this, currentFunction);
            emit(ifTrueLabel + ":");
            cbv.visitStatement(ctx.statement(0));
            emit(codeGoToEnd);

            emit(ifFalseLabel + ":");
            if(ctx.statement(1) != null ){
                cbv.visitStatement(ctx.statement(1));
            }            
            emit(codeGoToEnd);

            emit(endLabel + ":");
        }

        return code;
    }

    @Override
    public String visitIterationStatement(IterationStatementContext ctx) {
        String iterStatement = ctx.getText();       

        if(iterStatement.contains("while")) {
            // Do while loop
            JSONObject jsonExpression = jsonVisitor.visit(ctx.expression());
            Expression whileExpression = new Expression(jsonExpression);
            String expressionType = whileExpression.getType(this);

            if(!expressionType.equals("bool")) {
                Expression boolExpression = Expression.castTo(whileExpression, "bool");
                whileExpression = boolExpression;
                expressionType = whileExpression.getType(this);
            }
            
            
            String labelId = getLabel();
            String labelReset = String.join("", "label.",labelId,".whileReset");
            String startLabel = String.join("", "label.",labelId,".whileStart");
            String labelEnd = String.join("", "label.",labelId,".whileExit");

            breakLabels.add(labelEnd);

            //brakeLabel = labelEnd;

            
            String goToReset = String.join(" ", 
                "br",
                "label",
                "%"+labelReset
            );

            emit(goToReset);
            emit(labelReset+":");
            whileExpression.parse(this);

            String whileExpressionEval = String.join(" ", 
                "br",
                CodeTranslator.typeConverter(expressionType),
                whileExpression.getExprIdentifier(this)+",",
                "label",
                "%"+startLabel + ",",
                "label",
                "%"+labelEnd
            );
            emit(whileExpressionEval);
            emit(startLabel+":");

            CodeBuilderVisitor nested = new CodeBuilderVisitor(this, currentFunction);
            nested.visitStatement(ctx.statement());

            
            emit(goToReset);
            emit(labelEnd+":");
            breakLabels.pop();
        }

        return code;
    }

    public String getLabel() {
        if(getCurrentFunction().equals(parent.getCurrentFunction())) {
            return parent.getLabel();
        }
        return String.valueOf(labelCounter++);
    }

    public String searchBreakLabel() {
        if(parent != null) {
            return parent.searchBreakLabel();
        } else if (breakLabels.empty()) {
            return null;
        } else {
            return breakLabels.peek();
        }
    }

    public String getCurrentFunction() {
        if(currentFunction != null) {
            return currentFunction.getIdentifier();
        }

        return null;
    }

    


    


    

}
