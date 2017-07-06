package com.krgamestudios.lox;

import java.util.List;

abstract class Expr {
  interface Visitor<R> {
    R Visit(Assign expr);
    R Visit(Binary expr);
    R Visit(Grouping expr);
    R Visit(Literal expr);
    R Visit(Logical expr);
    R Visit(Unary expr);
    R Visit(Variable expr);
  }

  abstract <R> R Accept(Visitor<R> visitor);

  static class Assign extends Expr {
    final Token name;
    final Expr value;

    Assign(Token name,Expr value) {
      this.name = name;
      this.value = value;
    }

    <R> R Accept(Visitor<R> visitor) {
      return visitor.Visit(this);
    }
  }

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

  static class Logical extends Expr {
    final Expr lhs;
    final Token operator;
    final Expr rhs;

    Logical(Expr lhs,Token operator,Expr rhs) {
      this.lhs = lhs;
      this.operator = operator;
      this.rhs = rhs;
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

  static class Variable extends Expr {
    final Token name;

    Variable(Token name) {
      this.name = name;
    }

    <R> R Accept(Visitor<R> visitor) {
      return visitor.Visit(this);
    }
  }
}
