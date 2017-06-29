package com.krgamestudios.lox;

import java.util.List;

abstract class Expr {

  static class Binary extends Expr {
    final Expr lhs;
    final Token operator;
    final Expr rhs;

    Expr(Expr lhs,Token operator,Expr rhs) {
      this.lhs = lhs;
      this.operator = operator;
      this.rhs = rhs;
    }
  }

  static class Grouping extends Expr {
    final Expr expression;

    Expr(Expr expression) {
      this.expression = expression;
    }
  }

  static class Literal extends Expr {
    final Object value;

    Expr(Object value) {
      this.value = value;
    }
  }

  static class Unary extends Expr {
    final Token operator;
    final Expr rhs;

    Expr(Token operator,Expr rhs) {
      this.operator = operator;
      this.rhs = rhs;
    }
  }
}
