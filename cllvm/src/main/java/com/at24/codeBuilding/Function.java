package com.at24.codeBuilding;

import java.util.List;
import java.util.Map;

public class Function {
    String returnType;
    String identidier;
    List<String> paramTypes;
    Map<String, String> paramNameToRegister;
    
    public boolean hasParam(String param) {
        return paramNameToRegister.containsKey(param);
    }
}
