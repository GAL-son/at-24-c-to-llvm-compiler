package com.at24.translationTools;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class CodeBuilder {
    private static class VarData {
        public String identifier;
        public boolean isConst;
        public String type;
        public InitializerData initializer;

        public VarData(JSONObject json) {
            
        }

        public static JSONArray splitJson(JSONObject json) {
            JSONArray array = new JSONArray();


            return array;
        }
    }

    private class InitializerData {

    }

    public static String buildVar(JSONObject json) {
        VarData data = new VarData(json);
        String directVar = "";
        String beforeVar = "";

        



        return beforeVar + directVar;
    }

    public static String buildVar(JSONObject json, boolean isGlobal) {
        if(!isGlobal) {
            return buildVar(json);
        }

        VarData data = new VarData(json);
        String varName = "@" + data.identifier;

        return "";
    }

    public static String buildSingleVar(JSONObject json) {
        return "";
    }
}
