package com.krgamestudios.lox;

import java.util.List;

import static com.krgamestudios.lox.TokenType.*;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
  private Environment environment = new Environment();

  //wrapper functions
  void Interpret(List<Stmt> statements) {
    try {
      for (Stmt statement : statements) {
        Execute(statement);
      }
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

  void ExecuteBlock(List<Stmt> statements, Environment environment) {
    Environment previous = this.environment;

    try {
      this.environment = environment;
      for (Stmt statement : statements) {
        Execute(statement);
      }
    }
    finally {
      this.environment = previous;
    }
  }

  //AST types
  @Override
  public Object Visit(Expr.Assign expr) {
    Object value = Evaluate(expr.value);
    environment.Assign(expr.name, value);
    return value;
  }

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

  @Override
  public Object Visit(Expr.Logical expr) {
    Object lhs = Evaluate(expr.lhs);

    if (expr.operator.type == TokenType.OR) {
      if (IsTruthy(lhs)) return lhs;
    }
    if (!IsTruthy(lhs)) {
      return lhs;
    }

    return Evaluate(expr.rhs);
  }

  @Override
  public Object Visit(Expr.Variable expr) {
    return environment.Get(expr.name);
  }

  @Override
  public Void Visit(Stmt.Var stmt) {
    Object value = null;
    if (stmt.initializer != null) {
      value = Evaluate(stmt.initializer);
    }
    environment.Define(stmt.name.lexeme, value);
    return null;
  }

  @Override
  public Void Visit(Stmt.While stmt) {
    while(IsTruthy(Evaluate(stmt.condition))) {
      Execute(stmt.body);
    }
    return null;
  }

  @Override
  public Void Visit(Stmt.Block stmt) {
    ExecuteBlock(stmt.statements, new Environment(environment));
    return null;
  }

  @Override
  public Void Visit(Stmt.Expression stmt) {
    Evaluate(stmt.expression);
    return null;
  }

  @Override
  public Void Visit(Stmt.If stmt) {
    if (IsTruthy(Evaluate(stmt.condition))) {
      Execute(stmt.thenBranch);
    }
    else if (stmt.thenBranch != null) {
      Execute(stmt.thenBranch);
    }
    return null;
  }

  @Override
  public Void Visit(Stmt.Print stmt) {
    Object value = Evaluate(stmt.expression);
    System.out.println(Stringify(value));
    return null;
  }

  //helpers
  private void Execute(Stmt stmt) {
    stmt.Accept(this);
  }

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
