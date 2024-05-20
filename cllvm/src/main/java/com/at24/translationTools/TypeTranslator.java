package com.at24.translationTools;

public class TypeTranslator {

    public static String typeConverter(String typeName){
        switch (typeName) {
            case "int":
                return "i32";
            case "long":
                return "i64";
            case "short":
                return "i16";
            case "float":
                return "f32";
            case "double":
                return "f64";
            case "void":
                return "void";
            case "char":
                return "i8";
        }
      return null;
    };

}
