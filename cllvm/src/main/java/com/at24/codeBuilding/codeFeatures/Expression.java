package com.at24.codeBuilding.codeFeatures;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.print.attribute.HashAttributeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.at24.CParser.ForExpressionContext;
import com.at24.codeBuilding.CodeTranslator;
import com.at24.codeBuilding.codeFeatures.variables.Variable;

public class Expression implements Parsable{
    public boolean isVariable = false;
    String value = null;
    String varName = null;
    List<String> operators = new LinkedList<>();
    List<Expression> expressions = new LinkedList<>();

    public Expression(JSONObject initializer) {
        System.out.println("EXPR" + initializer);
        // Check direct constant
        if(initializer.has("Constant")) {
            this.value = initializer.getString("Constant");
            System.out.println(this);
            return;
        } 

        // Check direct identidier
        if(initializer.has("identifier")) {
            this.varName = initializer.getString("identifier");
            System.out.println(this);
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

        System.out.println(this);
    }

    @Override
    public void parse(CodeContext context) throws RuntimeException {
        System.out.println("------------------\n\nCurrent Expression");
        System.out.println(this + "\n");
        if(!isExpression()) {
            System.out.println("Not expression");
            return;
        }
        System.out.println("expression");
        System.out.println("Child Expressions: " + expressions.size());

        for (Expression expression : expressions) {
            System.out.println("CHILD");
            expression.parse(context);
        }
        String lastReg = null;
        int exprIndex = 0;

        System.out.println("LOOP START" + operators.size());
        for (String operator : operators) {
            System.out.println("OPERATOR ITER ===================");
            String operation = CodeTranslator.operationConverter(operator);
            System.err.println(operation);
            String regFirst = null;
            String regSecond = null;

            if(lastReg == null) {
                System.out.println("ChainFirst");
                Expression first = expressions.get(exprIndex);
                System.out.println("ChainFirst is Const" + first.isConst());
                regFirst = first.getExprIdentifier(context);;
                exprIndex++;
                if(regFirst == null) {
                    throw new RuntimeException("Missing declaration");
                }
            } else {
                regFirst = lastReg;
            }

            Expression second = expressions.get(exprIndex++);
            regSecond = second.getExprIdentifier(context);

            System.out.println("CURRENT OPERATION"); 
            System.out.println(operation); 
            System.out.println("FirtstArg - " + regFirst); 
            System.out.println("SecondArg - " + regSecond); 
            
            String operationCode = String.join(" ", 
                    operation, 
                    getType(),
                    regFirst + ",",
                    regSecond
                );

            //System.out.println(operationCode);
            lastReg = saveToRegister(context, operationCode);            
        }

        
        // for (Expression expression : expressions) {
        //     //System.out.println("NESTED EXPRESION" + expression.toString());
        //     if(expression.isExpression()) {
        //         expression.parse(context);
        //     } 
        // }       
        // System.out.println("EXPRESION");

        // String lastReg = null;
        // int currentOperationIndex = 0;

        // for (String operator : operators) {
        //     String operation = CodeTranslator.operationConverter(operator);
        //     String operationCode = "";
        //     Expression first;
        //     String regFirst;
            
        //     if(lastReg == null) {
        //         System.out.println("CURRENT Index" + currentOperationIndex); 
        //         first = expressions.get(currentOperationIndex++);
        //         regFirst = first.getExprIdentifier(context);
        //     }  else {
        //         regFirst = lastReg;
        //     }
            
            
        //     Expression second = expressions.get(currentOperationIndex++);
        //     String regSecond = second.getExprIdentifier(context);          
            
        //     System.out.println("CURRENT OPERATION"); 
        //     System.out.println(operation); 
        //     System.out.println("FirtstArg - " + regFirst); 
        //     System.out.println("SecondArg - " + regSecond); 

            

        //     operationCode = String.join(" ", 
        //         operation, 
        //         getType(),
        //         "%" + regFirst + ",",
        //         "%" + regSecond
        //     );

        //     lastReg = saveToRegister(context, operationCode);
        // }
        
        // context.assignRegister(this);
    }

    private String getExprIdentifier(CodeContext context) {
        System.out.println("GET REG EXPR + " + this);
        String ret = "";
        if(isConst()) {
            ret = getValue();
        } else if (isExpression()) {
            System.out.println("Is Const");
            ret = "%" + context.getRegisterName(this);
        } else {
            if(context.isVariableFunctionArg(varName)) {
                ret = "%"+varName;
            } else {
                Variable var = context.searchVariable(varName);
                ret = "%"+var.readFomVariable(context);
            }


            
        }
        System.out.println("EXPR IDENTIFER " + ret);
        return ret;
    }

    public boolean isConst() {
        System.out.println("\nCHECK CONST");
        System.out.println(this);
        System.out.println("value != null ");
        System.out.println((value != null));
        if(value != null) {
            System.out.println("!value.isEmpty()" + !value.isEmpty());
        }
        return value != null && (!value.isEmpty());
    }

    public String getType() {
        return "EXPRTYPE";
    }    

    public String getVariable() {
        return varName;
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
        String regName = "%"+ context.assignRegister(this);
        context.emit(regName + " = " + code);

        return regName;
    }

    @Override
    public String toString() {
        String ret = "[EXPR: \n";
        ret += "Value: " + value + "\n";
        ret += "Variable: " + varName + "\n";
        ret += "operators: " + operators.toString() + "\n";
        ret += "expressions: " + expressions.size() + "\n";
        return ret + "]";
    }

  



}
