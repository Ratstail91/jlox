package com.krgamestudios.lox;

import java.lang.StringBuilder;

//create a lisp-like representation of AST nodes
class AstPrinter implements Expr.Visitor<String> {
  String print(Expr expr) {
    return expr.accept(this);
  }

  //define the visit methods
  @Override
  public String visit(Expr.Binary expr) {
    return Parenthesize(expr.operator.lexeme, expr.lhs, expr.rhs);
  }

  @Override
  public String visit(Expr.Grouping expr) {
    return Parenthesize("group", expr.expression);
  }

  @Override
  public String visit(Expr.Literal expr) {
    if (expr.value == null) return "nil";
    return expr.value.toString();
  }

  @Override
  public String visit(Expr.Unary expr) {
    return Parenthesize(expr.operator.lexeme, expr.rhs);
  }

  private String Parenthesize(String name, Expr... exprs) {
    StringBuilder builder = new StringBuilder();

    builder.append("(").append(name);
    for (Expr expr : exprs) {
      builder.append(" ");
      builder.append(expr.accept(this));
    }
    builder.append(")");

    return builder.toString();
  }
}
