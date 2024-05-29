package com.at24.codeBuilding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.json.JSONArray;
import org.json.JSONObject;

import com.at24.CBaseVisitor;
import com.at24.CParser;
import com.at24.CParser.DeclarationContext;
import com.at24.CParser.FunctionDefinitionContext;
import com.at24.codeBuilding.codeFeatures.CodeContext;
import com.at24.codeBuilding.codeFeatures.Function;
import com.at24.codeBuilding.codeFeatures.Variable;
import com.at24.exceptions.DoubleDeclarationException;
import com.at24.visitors.JSONVisitor;

public class CodeBuilderVisitor extends CBaseVisitor<String> implements CodeContext {
    String code;
    Map<String, Variable> vars;
    Map<String, Function> funcs;
    CodeBuilderVisitor parent = null;
    Function currentFunction = null;

    public CodeBuilderVisitor() {
        code = "";
        vars = new HashMap<>();
    }

    public CodeBuilderVisitor(CodeBuilderVisitor parent, Function func) {
        super();
        this.parent = parent;
        currentFunction = func;
    }

    public String getCode() {
        return code;
    }

    @Override
    public void emit(String emit) {
        code += emit + "\n";
    }

    @Override
    public Variable searchVariable(String variableName) {
        // Search variable in current context
        if (vars.containsKey(variableName)) {
            return vars.get(variableName);
        } else if (parent == null) { 
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
        if (parent != null) {
            return parent.searchFunction(functionName);
        }

        if(funcs.containsKey(functionName)) {
            return funcs.get(functionName);
        } else {
            return null;
        }
    }

    private void declareVariable(JSONObject variableDeclaration) {
        Variable var = new Variable(variableDeclaration);
        if (vars.containsKey(var.identifier)) {
            throw new DoubleDeclarationException();
        }

        if (var.dependsOn != null && var.dependsOn.size() > 0) {
            for (String dependVariable : var.dependsOn) {
                if (!variableExists(dependVariable)) {
                    throw new RuntimeException("Missing variable declaration: " + dependVariable);
                }
            }
        }

        vars.put(var.identifier, var);

        if (parent == null) {
            // Global context
            emit(var.parseGlobal());
        } else {
            emit(var.parseLocal());
        }
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
        JSONObject declaration = new JSONVisitor().visitDeclaration(ctx);
        if (Variable.isDeclarationVariable(declaration)) {
            // Do variable declaration
            try {
                declareVariable(declaration);
            } catch (RuntimeException e) {
                throw new RuntimeException(e.getMessage() + " At line: " + getLine(ctx));
            }
        } else { // Check for function declaration
            emit("FUNC");
        }

        return code;
    }

    @Override
    public String visitFunctionDefinition(FunctionDefinitionContext ctx) {
        JSONVisitor visitor = new JSONVisitor();
        JSONObject declarationSpecifiers = visitor.visitDeclarationSpecifiers(ctx.declarationSpecifiers());
        JSONObject declarator = visitor.visitDeclarator(ctx.declarator());

        return code;
    }

}
