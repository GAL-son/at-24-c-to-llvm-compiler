package com.at24.codeBuilding.codeFeatures.variables;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VariableTreeReader {
    public static boolean isDeclarationVariable(JSONObject declaration) {

        JSONObject declarator = getDeclarator(declaration);
        // System.out.println("isDeclarationVariable" + declarator);
        return declarator.has("Identifier");
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

    public static String getVariableIdentifier(JSONObject declarator) {
        return declarator.getString("Identifier");
    }

    public static JSONObject getInitializer(JSONObject declaration) {
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
