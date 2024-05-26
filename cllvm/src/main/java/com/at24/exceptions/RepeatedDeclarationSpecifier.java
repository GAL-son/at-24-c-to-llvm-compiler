package com.at24.exceptions;

import java.util.Set;

public class RepeatedDeclarationSpecifier extends SyntaxException {
    public RepeatedDeclarationSpecifier(Set<String> repeatedSpecifiers) {
        super("Repeated Specifiers: " + repeatedSpecifiers);
    }
}
