package com.krgamestudios.lox;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import static com.krgamestudios.lox.TokenType.*;

class Parser {
  private final List<Token> tokens;
  private int current = 0;

  Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  List<Stmt> Parse() {
    List<Stmt> statements = new ArrayList<>();
    while(!IsAtEnd()) {
      statements.add(Declaration());
    }
    return statements;
  }

  private void Synchronize() {
    Advance();

    while(!IsAtEnd()) {
      if (Previous().type == SEMICOLON) return;
      switch(Peek().type) {
        case CLASS:
        case FUN:
        case VAR:
        case FOR:
        case IF:
        case WHILE:
        case PRINT:
        case RETURN:
          return;
      }
      Advance();
    }
  }

  //parser rules
  private Stmt Declaration() {
    try {
      if (Match(VAR)) return VarDeclaration();
      return Statement();
    }
    catch(ParseError error) {
      Synchronize();
      return null;
    }
  }

  private Stmt VarDeclaration() {
    Token name = Consume(IDENTIFIER, "Expected variable name.");
    Expr initializer = null;
    if (Match(EQUAL)) {
      initializer = Expression();
    }
    Consume(SEMICOLON, "Expected ';' after variable declaration.");
    return new Stmt.Var(name, initializer);
  }

  private Stmt Statement() {
    if (Match(FOR)) return ForStatement();
    if (Match(IF)) return IfStatement();
    if (Match(PRINT)) return PrintStatement();
    if (Match(LEFT_BRACE)) return new Stmt.Block(Block());
    if (Match(WHILE)) return WhileStatement();
    return ExpressionStatement();
  }

  private Stmt ForStatement() {
    Consume(LEFT_PAREN, "Expected '(' after 'for'.");

    //initializer
    Stmt initializer;
    if (Match(SEMICOLON)) {
      initializer = null;
    }
    else if (Match(VAR)) {
      initializer = VarDeclaration();
    }
    else {
      initializer = ExpressionStatement();
    }

    //conditional
    Expr condition = null;
    if (!Check(SEMICOLON)) {
      condition = Expression();
    }
    Consume(SEMICOLON, "Expected ';' after loop condition.");

    //increment
    Expr increment = null;
    if (!Check(RIGHT_PAREN)) {
      increment = Expression();
    }
    Consume(RIGHT_PAREN, "Expected ')' after for clause.");

    Stmt body = Statement();

    //working backwards
    if (increment != null) {
      body = new Stmt.Block(Arrays.asList(body, new Stmt.Expression(increment)));
    }

    if (condition == null) {
      condition = new Expr.Literal(true);
    }
    body = new Stmt.While(condition, body);

    if (initializer != null) {
      body = new Stmt.Block(Arrays.asList(initializer, body));
    }

    return body;
  }

  private Stmt IfStatement() {
    Consume(LEFT_PAREN, "Expected '(' after 'if'.");
    Expr condition = Expression();
    Consume(RIGHT_PAREN, "Expected ')' after if condition.");

    Stmt thenBranch = Statement();
    Stmt elseBranch = null;
    if (Match(ELSE)) {
      elseBranch = Statement();
    }

    return new Stmt.If(condition, thenBranch, elseBranch);
  }

  private Stmt PrintStatement() {
    Expr value = Expression();
    Consume(SEMICOLON, "Expected ';' after print statement.");
    return new Stmt.Print(value);
  }

  private List<Stmt> Block() {
    List<Stmt> statements = new ArrayList<>();

    while(!Check(RIGHT_BRACE) && !IsAtEnd()) {
      statements.add(Declaration());
    }

    Consume(RIGHT_BRACE, "Expected '}' after block.");
    return statements;
  }

  private Stmt WhileStatement() {
    Consume(LEFT_PAREN, "Expected '(' after 'while'.");
    Expr condition = Expression();
    Consume(RIGHT_PAREN, "Expected ')' after while condition.");
    Stmt body = Statement();

    return new Stmt.While(condition, body);
  }

  private Stmt ExpressionStatement() {
    Expr value = Expression();
    Consume(SEMICOLON, "Expected ';' after statement.");
    return new Stmt.Expression(value);
  }

  private Expr Expression() {
    return Assignment();
  }

  private Expr Assignment() {
    Expr expr = Or();

    if (Match(EQUAL)) {
      Token equals = Previous();
      Expr value = Assignment();
      if (expr instanceof Expr.Variable) {
        Token name = ((Expr.Variable)expr).name;
        return new Expr.Assign(name, value);
      }
      Error(equals, "invalid assignment target.");
    }

    return expr;
  }

  private Expr Or() {
    Expr expr = And();

    while(Match(OR)) {
      Token operator = Previous();
      Expr rhs = And();
      expr = new Expr.Logical(expr, operator, rhs);
    }

    return expr;
  }

  private Expr And() {
    Expr expr = Equality();

    while(Match(AND)) {
      Token operator = Previous();
      Expr rhs = Equality();
      expr = new Expr.Logical(expr, operator, rhs);
    }

    return expr;
  }

  private Expr Equality() {
    Expr expr = Comparison();

    //right zero or more times
    while(Match(BANG_EQUAL, EQUAL_EQUAL)) {
      Token operator = Previous();
      Expr rhs = Comparison();
      expr = new Expr.Binary(expr, operator, rhs);
    }

    return expr;
  }

  private Expr Comparison() {
    Expr expr = Term();

    //right zero or more times
    while(Match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      Token operator = Previous();
      Expr rhs = Term();
      expr = new Expr.Binary(expr, operator, rhs);
    }

    return expr;
  }

  private Expr Term() {
    Expr expr = Factor();

    //right zero or more times
    while(Match(MINUS, PLUS)) {
      Token operator = Previous();
      Expr rhs = Factor();
      expr = new Expr.Binary(expr, operator, rhs);
    }

    return expr;
  }

  private Expr Factor() {
    Expr expr = Unary();

    //right zero or more times
    while(Match(SLASH, STAR)) {
      Token operator = Previous();
      Expr rhs = Unary();
      expr = new Expr.Binary(expr, operator, rhs);
    }

    return expr;
  }

  private Expr Unary() {
    if (Match(BANG, MINUS)) {
      Token operator = Previous();
      Expr rhs = Unary();
      return new Expr.Unary(operator, rhs);
    }

    return Primary();
  }

  private Expr Primary() {
    if (Match(FALSE)) return new Expr.Literal(false);
    if (Match(TRUE)) return new Expr.Literal(true);
    if (Match(NIL)) return new Expr.Literal(null);

    if (Match(NUMBER, STRING)) {
      return new Expr.Literal(Previous().literal);
    }

    if (Match(IDENTIFIER)) return new Expr.Variable(Previous());

    if (Match(LEFT_PAREN)) {
      Expr expr = Expression();
      Consume(RIGHT_PAREN, "Expected ')' after expression.");
      return new Expr.Grouping(expr);
    }

    throw Error(Peek(), "Expected expression.");
  }

  //helpers
  private boolean Match(TokenType... types) {
    for (TokenType type : types) {
      if (Check(type)) {
        Advance();
        return true;
      }
    }
    return false;
  }

  private boolean Check(TokenType type) {
    if (IsAtEnd()) return false;
    return Peek().type == type;
  }

  private Token Advance() {
    if (!IsAtEnd()) current++;
    return Previous();
  }

  private boolean IsAtEnd() {
    return Peek().type == EOF;
  }

  private Token Peek() {
    return tokens.get(current);
  }

  private Token Previous() {
    return tokens.get(current-1);
  }

  private Token Consume(TokenType type, String msg) {
    if (Check(type)) return Advance();
    throw Error(Peek(), msg);
  }

  //error handling
  private ParseError Error(Token token, String msg) {
    Lox.Error(token, msg);
    return new ParseError();
  }

  private static class ParseError extends RuntimeException {}
}
