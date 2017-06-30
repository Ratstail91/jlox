package com.krgamestudios.lox;

import java.lang.StringBuilder;

//create a reverse-polish notation representation of the AST
class AstPrinterPostfix implements Expr.Visitor<String> {
  String print(Expr expr) {
    return expr.accept(this);
  }

  //define the visit methods
  @Override
  public String visit(Expr.Binary expr) {
    return Display(expr.operator.lexeme, expr.lhs, expr.rhs);
  }

  @Override
  public String visit(Expr.Grouping expr) {
    return expr.expression.accept(this);
  }

  @Override
  public String visit(Expr.Literal expr) {
    if (expr.value == null) return "nil";
    return expr.value.toString();
  }

  @Override
  public String visit(Expr.Unary expr) {
    if (expr.operator.type == TokenType.MINUS) {
      return Display("neg", expr.rhs);
    }
    return Display(expr.operator.lexeme, expr.rhs);
  }

  private String Display(String name, Expr... exprs) {
    StringBuilder builder = new StringBuilder();

    for (Expr expr : exprs) {
      builder.append(expr.accept(this));
      builder.append(" ");
    }
    builder.append(name);

    return builder.toString();
    
  }
}
