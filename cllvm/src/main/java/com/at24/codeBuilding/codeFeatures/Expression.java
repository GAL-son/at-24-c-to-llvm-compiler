package com.at24.codeBuilding.codeFeatures;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.at24.CParser.ForExpressionContext;
import com.at24.codeBuilding.CodeTranslator;

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

    @Override
    public void parse(CodeContext context) throws RuntimeException {
        if(!isExpression()) {
            return;
        }

        for (Expression expression : expressions) {
            if(isExpression()) {
                expression.parse(context);
            }
        }

        String lastReg = null;
        int currentOperationIndex = 0;

        for (String operator : operators) {
            String operation = CodeTranslator.operationConverter(operator);
            String operationCode = "";
            Expression first;
            String regFirst;
            
            if(lastReg == null) {
                first = expressions.get(currentOperationIndex++);
                regFirst = (first.isExpression()) ? context.getRegisterName(first) : first.getValue();              
            }  else {
                regFirst = lastReg;
            }
            Expression second = expressions.get(currentOperationIndex++);
            String regSecond = (second.isExpression()) ? context.getRegisterName(second) : second.getValue();
            operationCode = String.join(" ", 
                operation, 
                getType(),
                regFirst + ",",
                regSecond
            );

            lastReg = saveToRegister(context, operationCode);
        }
        
        context.assignRegister(this);
    }

    public String getType() {
        return "EXPRTYPE";
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

    private String saveToRegister(CodeContext context, String code) {
        return context.borrowRegister();
    }

  



}
