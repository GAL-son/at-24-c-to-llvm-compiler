package com.at24.codeBuilding.codeFeatures.functions;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.at24.codeBuilding.CodeTranslator;
import com.at24.codeBuilding.codeFeatures.CodeContext;
import com.at24.codeBuilding.codeFeatures.Parsable;

public class Function implements Parsable {
    private class ParamData {
        String name;
        String type;

        ParamData(String name, String type) {
            this.type = type;
            this.name = name;
        }
    }

    String returnType;
    String identidier;
    List<ParamData> params = new LinkedList<>();
    boolean isDefined = false;
    boolean useDefinition = false;
    
    
    public Function(JSONObject obj, boolean isDefinition) {
        JSONObject funcData;
        if(isDefinition) {
            funcData = FunctionTreeReader.dataFromDefinition(obj);
        } else {
            funcData = FunctionTreeReader.getFunctionData(obj);
        }

        setData(funcData);
    }

    public String getIdentifier() {
        return identidier;
    }

    public boolean isDefined() {
        return isDefined;
    }

    public void define() {
        isDefined = true;
    }

    private void setData(JSONObject funcData) {
        System.out.println(" PARSED DATA" + funcData);
        this.identidier = funcData.getString("id");
        this.returnType = funcData.getString("returnType");
        JSONArray args = funcData.getJSONArray("parameters");
        
        for(int i = 0; i < args.length(); i++) {
            JSONObject arg = args.getJSONObject(i);
            params.add(new ParamData(arg.getString("id"), arg.getString("type")));
        }
    }

    public void parse(CodeContext context) throws RuntimeException {
        if(!context.isGlobal()) {
            throw new RuntimeException("Functions must be global");
        }

        if(!isDefined) {
            // outside functions
            String declarationCode = String.join(" ",
                "declare",
                CodeTranslator.typeConverter(returnType),
                "@"+identidier,
                "("
            );

            for (ParamData paramData : params) {
                declarationCode = String.join(" ", 
                    declarationCode,
                    CodeTranslator.typeConverter(paramData.type),
                    "%"+paramData.name,
                    (params.indexOf(paramData) < params.size() - 1) ? "," : ""
                );
            }

            declarationCode += ")";
            context.emitOnTop(declarationCode);
        } else {
            String declarationCode = String.join(" ",
                "define",
                CodeTranslator.typeConverter(returnType),
                "@"+identidier,
                "("
            );

            for (ParamData paramData : params) {
                declarationCode = String.join(" ", 
                    declarationCode,
                    CodeTranslator.typeConverter(paramData.type),
                    "%"+paramData.name,
                    (params.indexOf(paramData) < params.size() - 1) ? "," : ""
                );
            }

            declarationCode += ") {";
            context.emit(declarationCode);
        }
    }

    public void endDeclarationParse(CodeContext context)  {
        context.emit("}");
    }

    public boolean hasParam(String param) {
        for (ParamData paramData : params) {
            if(paramData.name.equals(param)) {
                return true;
            }
        }

        return false;
    }
    
}
