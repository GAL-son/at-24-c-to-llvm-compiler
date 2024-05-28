package com.at24.codeBuilding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.at24.CBaseVisitor;
import com.at24.CParser.DeclarationContext;
import com.at24.exceptions.DoubleDeclarationException;
import com.at24.visitors.JSONVisitor;

public class CodeBuilderVisitor extends CBaseVisitor<String> {
    String code;
    Map<String, Variable> vars;
    CodeBuilderVisitor parent = null;

    public CodeBuilderVisitor() {
        code = "";
        vars = new HashMap<>();
    }
    public CodeBuilderVisitor(CodeBuilderVisitor parent) {
        super();
        this.parent = parent;
    }

    public String getCode() {
        return code;
    }

    public void emit(String emit) {
        code += emit + "\n";
    }

    private void declareVariable(JSONObject variableDeclaration) {
        Variable var = new Variable(variableDeclaration);
        if(vars.containsKey(var.identifier)) {
            throw new DoubleDeclarationException();
        }        

        if(var.dependsOn != null && var.dependsOn.size() > 0) {
            for (String dependVariable : var.dependsOn) {
                // Check dependent variables and throw exception if missing
            }
        }

        vars.put(var.identifier, var);

        if(parent == null) {
            // Global context
            emit(var.parseGlobal());
        } else {
            emit(var.parseLocal());
        }
    }

    @Override
    public String visitDeclaration(DeclarationContext ctx) {

        JSONObject declaration = new JSONVisitor().visitDeclaration(ctx);

        if(Variable.isDeclarationVariable(declaration)) {
            // Do variable declaration
            declareVariable(declaration);
        } else { // Check for function declaration

        }

        return code;
    }
}
