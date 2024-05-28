package com.at24.codeBuilding;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Function {
    String returnType;
    String identidier;
    List<String> paramTypes;
    Map<String, String> paramNameToRegister;
    
    public Function(JSONObject declaration) {

    }

    public boolean hasParam(String param) {
        return paramNameToRegister.containsKey(param);
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
}
