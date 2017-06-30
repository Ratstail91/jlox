package com.krgamestudios.lox;

import java.util.List;

abstract class Expr {
  interface Visitor<R> {
    R Visit(Binary expr);
    R Visit(Grouping expr);
    R Visit(Literal expr);
    R Visit(Unary expr);
  }

  abstract <R> R Accept(Visitor<R> visitor);

  static class Binary extends Expr {
    final Expr lhs;
    final Token operator;
    final Expr rhs;

    Binary(Expr lhs,Token operator,Expr rhs) {
      this.lhs = lhs;
      this.operator = operator;
      this.rhs = rhs;
    }

    <R> R Accept(Visitor<R> visitor) {
      return visitor.Visit(this);
    }
  }

  static class Grouping extends Expr {
    final Expr expression;

    Grouping(Expr expression) {
      this.expression = expression;
    }

    <R> R Accept(Visitor<R> visitor) {
      return visitor.Visit(this);
    }
  }

  static class Literal extends Expr {
    final Object value;

    Literal(Object value) {
      this.value = value;
    }

    <R> R Accept(Visitor<R> visitor) {
      return visitor.Visit(this);
    }
  }

  static class Unary extends Expr {
    final Token operator;
    final Expr rhs;

    Unary(Token operator,Expr rhs) {
      this.operator = operator;
      this.rhs = rhs;
    }

    <R> R Accept(Visitor<R> visitor) {
      return visitor.Visit(this);
    }
  }
}
