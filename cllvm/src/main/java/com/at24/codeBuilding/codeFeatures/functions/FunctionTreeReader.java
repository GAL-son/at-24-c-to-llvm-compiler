package com.at24.codeBuilding.codeFeatures.functions;

import org.json.JSONArray;
import org.json.JSONObject;

public class FunctionTreeReader {
    public static JSONObject getFunctionData(JSONObject declaration)  {
        JSONObject funcData = new JSONObject();
        funcData.put("returnType", getType(declaration));
        
        JSONObject directDeclarator = getDirectDeclarator(declaration);

        funcData.put("id", directDeclarator.getString("id"));
        funcData.put("parameters", directDeclarator.getJSONArray("parameters"));

        return funcData;
    }

    public static JSONObject dataFromDefinition(JSONObject definition) {
        // System.out.println("DEFINMITIO(N )" + definition);
        JSONObject funcData = new JSONObject();
        funcData.put("returnType", getType(definition));

        JSONObject declarator = definition.getJSONObject("declarator");

        funcData.put("id", declarator.getJSONObject("directDeclarator").getString("Identifier"));
        JSONArray params = new JSONArray();
        if(declarator.has("parameters")) {
            params = cleanArgs(declarator.getJSONArray("parameters"));
        }
        funcData.put("parameters", params);

        return funcData;
    }

    public static String getType(JSONObject declaration) {
        return declaration.getJSONObject("declarationSpecifiers").getString("typeSpecifier");
        
    }

    public static JSONObject getDirectDeclarator(JSONObject declaration) {
        // DirectDeclarator
        JSONObject directDeclarator = new JSONObject();
        JSONObject delcarator = declaration.getJSONArray("initDeclaratorList").getJSONObject(0).getJSONObject("declarator");

        String id = delcarator.getJSONObject("directDeclarator").getString("Identifier");
        directDeclarator.put("id", id);
        if(delcarator.has("parameters")) {
            directDeclarator.put("parameters", cleanArgs(delcarator.getJSONArray("parameters")));
        } else {
            directDeclarator.put("parameters", new JSONArray());
        }

        return directDeclarator;
    }

    public static JSONArray cleanArgs(JSONArray args) {
        JSONArray newArgs = new JSONArray();

        for(int i = 0; i < args.length(); i++ ) {
            JSONObject oldArg = args.getJSONObject(i);
            if(oldArg.isEmpty()) {
                continue;
            }

            JSONObject newArg = new JSONObject();
            String id = null;
            String type = null;
            if(oldArg.has("identifier")) {
                id = oldArg.getJSONObject("identifier").getString("Identifier");
                type = oldArg.getJSONObject("type").getString("typeSpecifier");
            } else {
                type = oldArg.getString("typeSpecifier");
            }

            newArg.put("id", id);
            newArg.put("type", type);

            newArgs.put(newArg);
        }
        
        return newArgs;
    }
}
