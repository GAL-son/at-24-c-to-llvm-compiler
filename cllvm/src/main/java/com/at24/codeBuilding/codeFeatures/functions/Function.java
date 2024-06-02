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
import com.at24.codeBuilding.codeFeatures.Expression;
import com.at24.codeBuilding.codeFeatures.Parsable;
import com.at24.codeBuilding.codeFeatures.variables.Variable;
import com.at24.exceptions.SyntaxException;

public class Function implements Parsable {
    private class ParamData {
        String name;
        String type;

        ParamData(String name, String type) {
            this.type = type;
            this.name = name;
        }

        @Override
        public String toString() {
            return String.join("", 
                "<",
                type,
                ",",
                name,
                ">"
            );
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

    public String callFunction(CodeContext context, List<String> args) {
        if(args.size() != params.size()) {
            throw new SyntaxException("Bad number of arguments!");
        }

        String callCode = String.join(" ",
            "call",
            CodeTranslator.typeConverter(returnType),
            "@"+identidier,
            "("
        );

        for(int i = 0; i < params.size(); i++) {
            callCode += CodeTranslator.typeConverter(params.get(i).type) + " " + args.get(i);    
            if(i < params.size() -1 )         {
                callCode += ", ";
            }
        }

        callCode += ")";
        return callCode;
    }

    public String callFunctionWithRegister(CodeContext context, List<String> args) {
        String register = context.borrowRegister();
        if(args.size() != params.size()) {
            throw new SyntaxException("Bad number of arguments!");
        }

        String callCode = String.join(" ",
            "%" + register,
            "=",
            callFunction(context, args)
        );

        context.emit(callCode);
        return register;
    }

    public void buildReturn(CodeContext context, JSONObject returnExpr) {
        Expression retExpr = new Expression(returnExpr);
        String retVal = "";
        boolean isConst = false;
        if(retExpr.isExpression()) {
            // Do the expression thing
            
            retExpr.parse(context);
            retVal = context.getRegisterName(retExpr);

        } else if (retExpr.isConst()) {
            // Set string 
            retVal = retExpr.getValue();
            isConst = true;
        } else {
            // Is variable
            String varName = retExpr.getVariable();
            Variable var = context.searchVariable(varName);

            String regName = var.readFomVariable(context);
            retVal = regName;
        }

        String returnStatemet = String.join(" ", 
            "ret",
            CodeTranslator.typeConverter(returnType),
            ((isConst) ? "" : "%")+retVal
        );

        context.emit(returnStatemet);
    }

    private void setData(JSONObject funcData) {
        // System.out.println(" PARSED DATA" + funcData);
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

    public Variable getParamVariable(String varName) {
        if(!hasParam(varName)) {
            return null;
        } 
        ParamData param = getParam(varName);
        if(param == null) {
            return null;
        }

        Variable var = new Variable();
        var.type = param.type;
        var.identifier = param.name;
        var.isGlobal = false;       

        return var;
    }

    public boolean hasParam(String param) {
        for (ParamData paramData : params) {
            if(paramData.name.equals(param)) {
                return true;
            }
        }

        return false;
    }

    private ParamData getParam(String paramName) {
        for (ParamData paramData : params) {
            if(paramData.name.equals(paramName)) {
                return paramData;
            }
        }

        return null;
    }
    
}
