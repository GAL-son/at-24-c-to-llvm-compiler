package com.at24.codeBuilding;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Variable implements Parsable {
    public String type;
    public String identifier;
    public List<String> dependsOn;
    public Expression initializator;
    public boolean isConst = false;
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
        if (!isDeclarationVariable(variableDeclaration)) {
            throw new RuntimeException("Used non variable declaration");
        }

        // Name
        JSONObject declarator = getDeclarator(variableDeclaration);
        System.out.println(declarator + " ID:" + getVariableIdentifier(declarator));
        this.identifier = getVariableIdentifier(declarator);

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
        JSONObject initializatorJsonObject = getInitializer(variableDeclaration);
        this.initializator = new Expression(initializatorJsonObject);
    }

    public int getNumberOfAssignments() {
        return numberOfAssignment;
    }

    @Override
    public String parseLocal() {
        String variableCode = "";

        String base = "%" + identifier + " = ";

        if(!initializator.isExpression()) {
            base += "add " + TypeTranslator.typeConverter(type) + " 0, " + initializator.value;
            variableCode += base;
        } else {
            
        }

        return variableCode;
    }

    @Override
    public String parseGlobal() {
        String base = "";
        System.out.println(type);
        base += "@" +
                this.identifier + 
                " = " +
                ((isConst) ? "constant" : "global") + " " + 
                TypeTranslator.typeConverter(this.type);

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

    public static boolean isDeclarationVariable(JSONObject declaration) {
        System.out.println(declaration);
        Set<String> declaratorKeys = getDeclarator(declaration).keySet();

        return declaratorKeys.size() == 1;
    }

    public static JSONObject getDeclarator(JSONObject declaration) {
        JSONArray initDeclaratorList = null;
        try {
            initDeclaratorList = declaration.getJSONArray("initDeclaratorList");
        } catch (JSONException e) {
            throw new RuntimeException("Missing initDeclaratorList");
        }

        if (initDeclaratorList.length() != 1) {
            throw new RuntimeException("Mulitiple Declarators");
        }

        return initDeclaratorList.getJSONObject(0).getJSONObject("declarator");
    }

    private static String getVariableIdentifier(JSONObject declarator) {
        return declarator.getJSONObject("directDeclarator").getString("Identifier");
    }

    private static JSONObject getInitializer(JSONObject declaration) {
        JSONArray initDeclaratorList = null;
        try {
            initDeclaratorList = declaration.getJSONArray("initDeclaratorList");
        } catch (JSONException e) {
            throw new RuntimeException("Missing initDeclaratorList");
        }

        if (initDeclaratorList.length() != 1) {
            throw new RuntimeException("Mulitiple Declarators");
        }

        return initDeclaratorList.getJSONObject(0).getJSONObject("initializer");
    }

}
