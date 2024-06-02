package com.at24.codeBuilding;

public class CodeTranslator {
    

    public static String typeConverter(String typeName){
        switch (typeName) {
            case "bool": 
                return "i1";
            case "int":
                return "i32";
            case "float":
                return "f32";
            case "double":
                return "f64";
            case "short":
                return "i16";
            case "void":
                return "void";
        }
      return null;
    };
    
    public static boolean compareTypes(String type1, String type2) {
        int type1val = getTypeValue(type1);
        int type2val = getTypeValue(type2);
        return type1val > type2val;
    }

    private static int getTypeValue(String type) {
        int value = 0;
        switch (type) {
            case "bool": 
                value = 0;
                break;
            case "char":
                value = 1;
                break;
            case "short":
                value = 2;
                break;
            case "int":
                value = 3;
                break;
            case "long":
                value = 4;
                break;
            case "float":
                value = 5;
                break;
            case "double":
                value = 6;
                break;    
        }

        return value;
    }

    public static String operationConverter(String operation) {
        switch (operation) {
            case "==":
                return "eq";
            case "!=":
                return "oeq";
            case "+":
                return "add";
            case "-":
                return "sub";
            case "*":
                return "mul";
            case "/":
                return "udiv";
        
            default:
                return "";
        }
    }

    public static boolean isBooleanOperation(String operator) {
        switch (operator) {
            case "==":
            case "!=":
                return true;
            default:
                return false;
        }
    }
    
    public static boolean isInteger(String type) {
        type = typeConverter(type);

        return type.contains("i");
    }

}
