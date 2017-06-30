package com.krgamestudios.lox;

import static com.krgamestudios.lox.TokenType.*;

class Interpreter implements Expr.Visitor<Object> {
  //wrapper functions
  void Interpret(Expr expression) {
    try {
      Object value = Evaluate(expression);
      System.out.println(Stringify(value));
    }
    catch(RuntimeError error) {
      Lox.RuntimeError(error);
    }
  }

  private String Stringify(Object object) {
    if (object == null) return "nil";

    //hack around java's ".0" for doubles
    if (object instanceof Double) {
      String text = object.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }

    return object.toString();
  }

  //AST types
  @Override
  public Object Visit(Expr.Literal expr) {
    return expr.value;
  }

  @Override
  public Object Visit(Expr.Grouping expr) {
    return Evaluate(expr.expression);
  }

  @Override
  public Object Visit(Expr.Unary expr) {
    Object rhs = Evaluate(expr.rhs);

    //handle special cases
    switch(expr.operator.type) {
      case MINUS:
        CheckNumberOperand(expr.operator, rhs); //distinct
        return -(double)rhs;
      case BANG:
        return !IsTruthy(rhs);
    }

    //unreachable
    return null;
  }

  @Override
  public Object Visit(Expr.Binary expr) {
    Object lhs = Evaluate(expr.lhs);
    Object rhs = Evaluate(expr.rhs);

    switch (expr.operator.type) {
      //equality operators
      case BANG_EQUAL: return !IsEqual(lhs, rhs);

      case EQUAL_EQUAL: return IsEqual(lhs, rhs);

      //comparison operators
      case GREATER:
        CheckNumberOperands(expr.operator, lhs, rhs);
        return (double)lhs > (double)rhs;

      case GREATER_EQUAL:
        CheckNumberOperands(expr.operator, lhs, rhs);
        return (double)lhs >= (double)rhs;

      case LESS:
        CheckNumberOperands(expr.operator, lhs, rhs);
        return (double)lhs < (double)rhs;

      case LESS_EQUAL:
        CheckNumberOperands(expr.operator, lhs, rhs);
        return (double)lhs <= (double)rhs;

      //arithmetic operators
      case MINUS:
        CheckNumberOperands(expr.operator, lhs, rhs);
        return (double)lhs - (double)rhs;

      case PLUS:
        if(lhs instanceof Double && rhs instanceof Double) {
          return (double)lhs + (double)rhs;
        }
        if(lhs instanceof String && rhs instanceof String) {
          return (String)lhs + (String)rhs;
        }
        throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");

      case SLASH:
        CheckNumberOperands(expr.operator, lhs, rhs);
        return (double)lhs / (double)rhs;

      case STAR:
        CheckNumberOperands(expr.operator, lhs, rhs);
        return (double)lhs * (double)rhs;
    }

    //unreachable
    return null;
  }

  //helpers
  private Object Evaluate(Expr expr) {
    return expr.Accept(this);
  }

  private boolean IsTruthy(Object object) {
    if (object == null) return false;
    if (object instanceof Boolean) return (boolean)object;
    return true;
  }

  private boolean IsEqual(Object a, Object b) {
    if (a == null && b == null) return true;
    if (a == null) return false;
    return a.equals(b);
  }

  private void CheckNumberOperand(Token operator, Object rhs) {
    if (rhs instanceof Double) return;
    throw new RuntimeError(operator, "Operand must be a number.");
  }

  private void CheckNumberOperands(Token operator, Object lhs, Object rhs) {
    if (lhs instanceof Double && rhs instanceof Double) return;
    throw new RuntimeError(operator, "Operands must be a number.");
  }
}
