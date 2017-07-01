package com.krgamestudios.lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
  private final Map<String, Object> values = new HashMap();

  void Define(String name, Object value) {
    values.put(name, value);
  }

  void Assign(Token name, Object value) {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value);
      return;
    }
    throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
  }

  Object Get(Token name) {
    if (values.containsKey(name.lexeme)) {
      return values.get(name.lexeme);
    }
    throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
  }
}
