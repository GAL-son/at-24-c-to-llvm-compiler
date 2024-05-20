package com.at24.translationTools;

public class VarBuilder {
    public static String buildGlobal(String name, String type, String value) {
        return "@" + name + " = global " + type + " " + value + "\n";
    }
}
