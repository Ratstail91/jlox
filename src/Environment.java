package com.krgamestudios.lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
  private final Map<String, Object> values = new HashMap();
  final Environment parent;

  Environment() {
    parent = null;
  }

  Environment(Environment parent) {
    this.parent = parent;
  }

  void Define(String name, Object value) {
    values.put(name, value);
  }

  void Assign(Token name, Object value) {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value);
      return;
    }
    if (parent != null) {
      parent.Assign(name, value);
      return;
    }
    throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
  }

  Object Get(Token name) {
    if (values.containsKey(name.lexeme)) {
      return values.get(name.lexeme);
    }
    if (parent != null) {
      return parent.Get(name);
    }
    throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
  }
}
