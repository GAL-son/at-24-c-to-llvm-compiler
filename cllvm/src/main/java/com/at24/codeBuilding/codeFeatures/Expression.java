package com.at24.codeBuilding.codeFeatures;

import java.nio.charset.CoderMalfunctionError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.print.attribute.HashAttributeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.at24.CParser.ForExpressionContext;
import com.at24.codeBuilding.CodeBuilderVisitor;
import com.at24.codeBuilding.CodeTranslator;
import com.at24.codeBuilding.codeFeatures.functions.Function;
import com.at24.codeBuilding.codeFeatures.variables.Variable;
import com.at24.exceptions.SyntaxException;

public class Expression implements Parsable{
    public boolean isVariable = false;
    String value = null;
    String varName = null;
    String functionCall = null;
    List<Expression> args = null;
    List<String> operators = new LinkedList<>();
    List<Expression> expressions = new LinkedList<>();

    private Expression() {

    }

    public Expression(JSONObject initializer) {
        System.out.println("EXPR" + initializer);
        // Check function
        if(initializer.has("arguments")) {
            //System.out.println("CHECK FUNC");
            this.functionCall = initializer.getString("name");
            args = new LinkedList<>();
            JSONArray array = initializer.getJSONArray("arguments");
            for(int i = 0; i < array.length(); i++) {
                JSONObject data = array.getJSONObject(i);
                args.add(new Expression(data));
            }
            //System.out.println(this);
            return;
        }

        // Check direct constant
        if(initializer.has("Constant")) {
            this.value = initializer.getString("Constant");
            //System.out.println(this);
            return;
        } 

        // Check direct identidier
        if(initializer.has("identifier")) {
            this.varName = initializer.getString("identifier");
            //System.out.println(this);
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
        //System.out.println("------------------\n\nCurrent Expression");
        //System.out.println(this + "\n");
        if(!isExpression()) {
            //System.out.println("Not expression");
            return;
        }
        //System.out.println("expression");
        //System.out.println("Child Expressions: " + expressions.size());

        for (Expression expression : expressions) {
            //System.out.println("CHILD");
            expression.parse(context);
        }
        String lastReg = null;
        int exprIndex = 0;

        //System.out.println("LOOP START" + operators.size());
        for (String operator : operators) {
            //System.out.println("OPERATOR ITER ===================");
            String operation = CodeTranslator.operationConverter(operator);
            //System.err.println(operation);
            String regFirst = null;
            String regSecond = null;
            String firstType = "";
            String secondType = "";

            if(lastReg == null) {
                //System.out.println("ChainFirst");
                Expression first = expressions.get(exprIndex);
                //System.out.println("ChainFirst is Const" + first.isConst());
                regFirst = first.getExprIdentifier(context);;
                firstType = first.getType(context);
                exprIndex++;
                if(regFirst == null) {
                    throw new RuntimeException("Missing declaration");
                }
            } else {
                firstType = this.getType(context);
                regFirst = lastReg;
            }

            String comparison = "";
            

            Expression second = expressions.get(exprIndex++);
            regSecond = second.getExprIdentifier(context);

            secondType = second.getType(context);

            //System.out.println("CURRENT OPERATION"); 
            //System.out.println(operation); 
            //System.out.println("FirtstArg - " + regFirst); 
           // System.out.println("SecondArg - " + regSecond); 

            if(CodeTranslator.isBooleanOperation(operator)) {
                String maxType = (CodeTranslator.compareTypes(firstType, secondType)) ? firstType : secondType;
                comparison = (CodeTranslator.isInteger(maxType)) ? "icmp " : "fcmp ";
            }
            
            String finalType = CodeTranslator.compareTypes(firstType, secondType) ? firstType : secondType;
            String operationCode = String.join(" ", 
                    comparison+operation, 
                    CodeTranslator.typeConverter(finalType),
                    regFirst + ",",
                    regSecond
                );

            //System.out.println(operationCode);
            lastReg = saveToRegister(context, operationCode);            
        }
    }

    public String getExprIdentifier(CodeContext context) {
        // System.out.println("GET REG EXPR + " + this);
        String ret = "";
        if(isFunctionCall()) {
            Function funcCall = context.searchFunction(functionCall);
            if (funcCall == null) {
                throw new SyntaxException("Missing function declaration: " + functionCall);
            }

            List<String> argsStrings = new ArrayList<>(args.size());

            for (Expression arg : args) {
                argsStrings.add(arg.getExprIdentifier(context));
            }
            ret = "%" + funcCall.callFunctionWithRegister(context, argsStrings);
        } else if(isConst()) {
            ret = getValue();
        } else if (isExpression()) {
            ret = "%" + context.getRegisterName(this);
        } else {
            if(context.isVariableFunctionArg(varName)) {
                ret = "%"+varName;
            } else {
                Variable var = context.searchVariable(varName);
                ret = "%"+var.readFomVariable(context);
            }           
        }
        // System.out.println("EXPR IDENTIFER " + ret);
        return ret;
    }

    public boolean isFunctionCall() {
        return functionCall != null;
    }

    public boolean isConst() {
        // System.out.println((value != null));
        if(value != null) {
            // System.out.println("!value.isEmpty()" + !value.isEmpty());
        }
        return value != null && (!value.isEmpty());
    }

    public String getType(CodeContext context) {
        if(isFunctionCall()) {
            Function func;
            if((func = context.searchFunction(functionCall)) != null) {
                return func.getReturnType();
            }
        }
        for (String operator : operators) {
            if(CodeTranslator.isBooleanOperation(operator)) {
                // System.out.println("-----------------------------------------------------------------------------------"+operator);
                return "bool";
            }
        }
        String type = "";
        if(isConst()) {
            // Check values
            if(value.contains("\'")) {
                return "char";
            } else {
                return "int";
            }
        } else if(isVariable()) {
            Variable var = context.searchVariable(varName);

            if(var != null) {
                type = var.type;
            }
        } else {
            for (String operator : operators) {
                if(CodeTranslator.isBooleanOperation(operator)) {
                    return "bool";
                }
            }
            String exprType = "";
            for (Expression expression : expressions) {
                String subExprType = expression.getType(context);

                if(!CodeTranslator.compareTypes(exprType, subExprType)) {
                    exprType = subExprType;
                }
            }
            type = exprType;
        }

        return type;
    }    

    public boolean isVariable() {
        return varName != null;
    }

    public String getVariable() {
        return varName;
    }

    public boolean isNull() {
        // System.out.println("TEST " + value);

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
        ret += "Func call" + functionCall + "\n";
        if(functionCall != null) {
            ret += "funcArgs " + args.size() + "\n";
        }
        ret += "operators: " + operators.toString() + "\n";
        ret += "expressions: " + expressions.size() + "\n";
        return ret + "]";
    }

    public static Expression castTo(Expression expr, String type) {
        Expression cast = new Expression();
        if(type.equals("bool")) {
            cast.operators.add("!=");
            cast.expressions.add(expr);
            JSONObject zero = new JSONObject();
            zero.put("Constant", "0");
            cast.expressions.add(new Expression(zero));            
        }

        return cast;
    }

  



}
