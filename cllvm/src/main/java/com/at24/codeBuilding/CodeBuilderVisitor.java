package com.at24.codeBuilding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.json.JSONArray;
import org.json.JSONObject;

import com.at24.CBaseVisitor;
import com.at24.CParser;
import com.at24.CParser.CompilationUnitContext;
import com.at24.CParser.DeclarationContext;
import com.at24.CParser.FunctionDefinitionContext;
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

    int registerNumber = 1;

    public CodeBuilderVisitor() {
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
            parent.emit(emit);
            return;
        }
        code += emit + "\n";
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
        if(currentFunction.hasParam(variableName)) {
            return null;
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
        } else {
            return null;
        }
    }

    @Override
    public String borrowRegister() {
        return Integer.toString(registerNumber++);
    }

    @Override
    public String assignRegister(Expression expression) {
        regs.put(expression, Integer.toString(registerNumber++));
        return regs.get(expression);
    }

    private void declareVariable(JSONObject variableDeclaration) {
        Variable var = new Variable(variableDeclaration);

        // If current context has double declaration
        if (vars.containsKey(var.identifier)) {
            throw new DoubleDeclarationException();
        }

        // TODO: this shoud be inside variable
        if (var.dependsOn != null && var.dependsOn.size() > 0) {
            for (String dependVariable : var.dependsOn) {
                if (!variableExists(dependVariable)) {
                    throw new RuntimeException("Missing variable declaration: " + dependVariable);
                }
            }
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
        System.out.println("functionDeclaration " + functionDeclaration);
        Function func = new Function(functionDeclaration);
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

    private int getLine(ParserRuleContext ctx) {
        return ctx.getStart().getLine();
    }

    @Override
    public String visitDeclaration(DeclarationContext ctx) {
        System.out.println("CODE BUILDER + " + ctx.getText());
        JSONObject declaration = new JSONVisitor().visitDeclaration(ctx);
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

    // @Override
    // public String visitFunctionDefinition(FunctionDefinitionContext ctx) {
    //     JSONVisitor visitor = new JSONVisitor();
    //     JSONObject declarationSpecifiers = visitor.visitDeclarationSpecifiers(ctx.declarationSpecifiers());
    //     JSONObject declarator = visitor.visitDeclarator(ctx.declarator());

    //     return code;
    // }

    

}
