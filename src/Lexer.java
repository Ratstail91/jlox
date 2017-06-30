package com.krgamestudios.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.krgamestudios.lox.TokenType.*;

class Lexer {
  //members
  private final String source;
  private final List<Token> tokens = new ArrayList<>();

  private int start = 0;
  private int current = 0;
  private int line = 1;

  private static final Map<String, TokenType> keywords;

  static {
    //define the reserved words
    keywords = new HashMap<>();
    keywords.put("and",		AND);
    keywords.put("class",	CLASS);
    keywords.put("else",	ELSE);
    keywords.put("false",	FALSE);
    keywords.put("for",		FOR);
    keywords.put("fun",		FUN);
    keywords.put("if",		IF);
    keywords.put("nil",		NIL);
    keywords.put("or",		OR);
    keywords.put("print",	PRINT);
    keywords.put("return",	RETURN);
    keywords.put("super",	SUPER);
    keywords.put("this",	THIS);
    keywords.put("true",	TRUE);
    keywords.put("var",		VAR);
    keywords.put("while",	WHILE);
  }

  //methods
  Lexer(String source) {
    this.source = source;
  }

  //core methods
  List<Token> ScanTokens() {
    while(!IsAtEnd()) {
      //We are at the beginning of the next lexeme
      start = current;
      ScanToken();
    }

    //Finally
    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  private void ScanToken() {
    char c = Advance();
    switch(c) {
      //single characters
      case '(': AddToken(LEFT_PAREN); break;
      case ')': AddToken(RIGHT_PAREN); break;
      case '{': AddToken(LEFT_BRACE); break;
      case '}': AddToken(RIGHT_BRACE); break;
      case ',': AddToken(COMMA); break;
      case '.': AddToken(DOT); break;
      case '-': AddToken(MINUS); break;
      case '+': AddToken(PLUS); break;
      case ';': AddToken(SEMICOLON); break;
      case '*': AddToken(STAR); break;

      //double characters
      case '!': AddToken(Match('=') ? BANG_EQUAL : BANG); break;
      case '=': AddToken(Match('=') ? EQUAL_EQUAL : EQUAL); break;
      case '<': AddToken(Match('=') ? LESS_EQUAL : LESS); break;
      case '>': AddToken(Match('=') ? GREATER_EQUAL : GREATER); break;

      //hamdle slash and comments
      case '/':
        if (Match('/')) {
          while(Peek() != '\n' && !IsAtEnd())
            Advance();
        }
        else {
          AddToken(SLASH);
        }
      break;

      //whitespace
      case ' ':
      case '\r':
      case '\t':
        //ignore whitespace
      break;

      case '\n':
        line++;
      break;

      //string literals
      case '"': String(); break;

      //default
      default:
        if (IsDigit(c)) {
          Number();
        }
        else if (IsAlpha(c)) {
          Identifier();
        }
        else {
          Lox.Error(line, "Unexpected character '" + c + "'");
        }
      break;
    }
  }

  //subroutines
  private void String() {
    //move to the end of the string
    while(Peek() != '"' && !IsAtEnd()) {
      if (Peek() == '\n') //multiline strings
        line++;
      Advance();
    }

    //unterminated string
    if (IsAtEnd()) {
      Lox.Error(line, "Unterminated string");
      return;
    }

    //eat the "
    Advance();

    //trim the "
    String value = source.substring(start + 1, current - 1); //TODO: unescape characters if supported
    AddToken(STRING, value);
  }

  private void Number() {
    while(IsDigit(Peek()))
      Advance();

    //check for fractional numbers
    if (Peek() == '.' && IsDigit(PeekNext())) {
      Advance();
      while(IsDigit(Peek()))
        Advance();
    }

    AddToken(NUMBER, Double.parseDouble(source.substring(start, current)));
  }

  private void Identifier() {
    while(IsAlNum( Peek() ))
      Advance();

    //check for reserved words
    String text = source.substring(start, current);

    TokenType type = keywords.get(text);
    if (type == null) type = IDENTIFIER;

    AddToken(type);
  }

  //helpers
  private boolean IsAtEnd() {
    return current >= source.length();
  }

  private char Peek() {
    if (IsAtEnd()) return '\0';
    return source.charAt(current);
  }

  private char PeekNext() {
    if (current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  }

  private char Advance() {
    current++;
    return source.charAt(current-1);
  }

  private boolean Match(char expected) {
    //Conditional Advance()
    if (IsAtEnd()) return false;
    if (source.charAt(current) != expected) return false;
    current++;
    return true;
  }

  private boolean IsDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private boolean IsAlpha(char c) {
    return
      (c >= 'a' && c <= 'z') ||
      (c >= 'A' && c <= 'Z') ||
      (c == '_');
  }

  private boolean IsAlNum(char c) {
    return IsAlpha(c) || IsDigit(c);
  }

  private void AddToken(TokenType type) {
    AddToken(type, null);
  }

  private void AddToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }
}
