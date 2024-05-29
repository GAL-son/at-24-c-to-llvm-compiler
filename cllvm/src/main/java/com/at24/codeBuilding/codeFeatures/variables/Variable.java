package com.at24.codeBuilding.codeFeatures.variables;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.at24.codeBuilding.CodeTranslator;
import com.at24.codeBuilding.codeFeatures.CodeContext;
import com.at24.codeBuilding.codeFeatures.Expression;
import com.at24.codeBuilding.codeFeatures.Parsable;

public class Variable implements Parsable {
    public String type;
    public String identifier;
    public List<String> dependsOn;
    public Expression initializator;
    public boolean isConst = false;
    public boolean isGlobal = false;
    public int numberOfAssignment = 0;

    public boolean isDefined;

    public Variable() {
        this.type = "";
        identifier = "";
        dependsOn = new LinkedList<>();
        initializator = null;
    }

    public Variable(JSONObject variableDeclaration) {
        this();
        if (!VariableTreeReader.isDeclarationVariable(variableDeclaration)) {
            throw new RuntimeException("Used non variable declaration");
        }

        // Name
        JSONObject declarator = VariableTreeReader.getDeclarator(variableDeclaration);
        System.out.println(declarator + " ID:" + VariableTreeReader.getVariableIdentifier(declarator));
        this.identifier = VariableTreeReader.getVariableIdentifier(declarator);

        // type
        JSONObject declarationSpecifiers = variableDeclaration.getJSONObject("declarationSpecifiers");
        this.type = declarationSpecifiers.getString("typeSpecifier");
        if(declarationSpecifiers.has("typeQualifier")) {
            String typeQualifier = declarationSpecifiers.getString("typeQualifier");

            if(typeQualifier.equals("const")) {
                isConst = true;
            }
        }

        // Value
        JSONObject initializatorJsonObject = VariableTreeReader.getInitializer(variableDeclaration);
        this.initializator = new Expression(initializatorJsonObject);
    }

    public int getNumberOfAssignments() {
        return numberOfAssignment;
    }

    @Override
    public void parse(CodeContext context) throws RuntimeException {
        isGlobal = context.isGlobal();
        if(isGlobal) {
            context.emit(parseGlobal());
        } else {
            context.emit(parseLocal(context));
        }        
    }

    public String parseLocal(CodeContext context) {
        String variableCode = "";
        variableCode += "%" + identifier + " = alloca " + CodeTranslator.typeConverter(type);

        if(initializator != null) {
            storeToVariable(context, initializator);
        }       

        return variableCode;
    }

    public void storeToVariable(CodeContext context, Expression expr) {
        String varType = CodeTranslator.typeConverter(type);
        String assignType = expr.getType();
        
        String exprReg = "%" + context.getRegisterName(expr);

        String storeCode = String.join(" ", 
            "store",
            assignType,
            ((expr.isExpression()) ? exprReg : expr.getValue()) + ",",
            (varType+"*"),
            identifier
        );

        context.emit(storeCode);        
    }

    public String readFomVariable(CodeContext context) {
        String regName = context.borrowRegister();
        String storeCode = String.join(" ", 
            ("%"+regName),
            "=",
            "load",
            CodeTranslator.typeConverter(type) + ",",
            CodeTranslator.typeConverter(type)+ "*",
            ((isGlobal) ? "@" : "%")+ identifier
        );

        context.emit(storeCode);
        return regName;        
    }

    public String parseGlobal() {
        isGlobal = true;
        String base = "";
        System.out.println(type);
        base += "@" +
                this.identifier + 
                " = " +
                ((isConst) ? "constant" : "global") + " " + 
                CodeTranslator.typeConverter(this.type);

        System.out.println(initializator.isExpression());

        if (initializator == null || initializator.isNull()) {
            base += "* null";
        } else if(initializator.isExpression()) {
            throw new RuntimeException("Global variables must be initialized with constant");
        } else {                
            base += " "+initializator.getValue();
        }

        return base;
    }    

   

}
