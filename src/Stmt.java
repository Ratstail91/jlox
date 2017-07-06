package com.krgamestudios.lox;

import java.util.List;

abstract class Stmt {
  interface Visitor<R> {
    R Visit(Block stmt);
    R Visit(Expression stmt);
    R Visit(If stmt);
    R Visit(Print stmt);
    R Visit(Var stmt);
    R Visit(While stmt);
  }

  abstract <R> R Accept(Visitor<R> visitor);

  static class Block extends Stmt {
    final List<Stmt> statements;

    Block(List<Stmt> statements) {
      this.statements = statements;
    }

    <R> R Accept(Visitor<R> visitor) {
      return visitor.Visit(this);
    }
  }

  static class Expression extends Stmt {
    final Expr expression;

    Expression(Expr expression) {
      this.expression = expression;
    }

    <R> R Accept(Visitor<R> visitor) {
      return visitor.Visit(this);
    }
  }

  static class If extends Stmt {
    final Expr condition;
    final Stmt thenBranch;
    final Stmt elseBranch;

    If(Expr condition,Stmt thenBranch,Stmt elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    <R> R Accept(Visitor<R> visitor) {
      return visitor.Visit(this);
    }
  }

  static class Print extends Stmt {
    final Expr expression;

    Print(Expr expression) {
      this.expression = expression;
    }

    <R> R Accept(Visitor<R> visitor) {
      return visitor.Visit(this);
    }
  }

  static class Var extends Stmt {
    final Token name;
    final Expr initializer;

    Var(Token name,Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    <R> R Accept(Visitor<R> visitor) {
      return visitor.Visit(this);
    }
  }

  static class While extends Stmt {
    final Expr condition;
    final Stmt body;

    While(Expr condition,Stmt body) {
      this.condition = condition;
      this.body = body;
    }

    <R> R Accept(Visitor<R> visitor) {
      return visitor.Visit(this);
    }
  }
}
