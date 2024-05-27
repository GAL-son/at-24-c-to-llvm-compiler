package com.at24.codeBuilding;

public class TypeTranslator {
    public static String typeConverter(String typeName){
        System.out.println(typeName);
        switch (typeName) {
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
    //type

}
