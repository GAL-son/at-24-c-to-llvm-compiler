package com.at24.codeBuilding.codeFeatures;

public interface CodeContext {
    public void emit(String code);
    public Variable searchVariable(String variableName);
    public Function searchFunction(String functionName);
    
} 