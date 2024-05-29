package com.at24.codeBuilding.codeFeatures;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Expression implements Parsable{
    String value = null;
    List<String> operators = new LinkedList<>();
    List<Expression> expressions = new LinkedList<>();

    public Expression(JSONObject initializer) {
        System.out.println(initializer);
        if(initializer.has("Constant")) {
            this.value = initializer.getString("Constant");
            return;
        } 

        if(initializer.has("expressions")) {
            JSONArray arr = initializer.getJSONArray("expressions");
            if(arr.length() == 1 && arr.getJSONObject(0).has("Constant")) {
                this.value = initializer.getString("Constant");
                return;
            }

            for(int i = 0; i < arr.length(); i++) {
                JSONObject exprObject = arr.getJSONObject(i);
                if(exprObject.isEmpty()) {
                    continue;
                }

                Expression expr = new Expression(exprObject);
                expressions.add(expr);
            }
        }

        if(initializer.has("operators")) {
            JSONArray arr = initializer.getJSONArray("operators");
            for(int i = 0; i < arr.length(); i++) {
                String operator = arr.getString(i);
                operators.add(operator);
            }
        }        
        
    }

    public boolean isNull() {
        System.out.println("TEST " + value);

        if(value != null) {
            return  (value.equals("null") || value.equals("NULL"));
        }
        

        return !isExpression();
    }

    public boolean isExpression() {
        return expressions.size() > 0;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String parseLocal() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parseLocal'");
    }

    @Override
    public String parseGlobal() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parseGlobal'");
    }


}
