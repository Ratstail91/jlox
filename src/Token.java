package com.krgamestudios.lox;

class Token {
  //Members
  final TokenType type;
  final String lexeme;
  final Object literal;
  final int line;

  //Methods
  Token(TokenType type, String lexeme, Object literal, int line) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.line = line;
  }

  public String toString() { //toString is used by System.out.println
    return type + " " + lexeme + " " + literal;
  }
}
