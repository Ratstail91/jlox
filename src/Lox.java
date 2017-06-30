package com.krgamestudios.lox;

//exceptions
import java.io.IOException;

//read in data
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;

public class Lox {
  //members
  static boolean errorState = false;
  private static final Interpreter interpreter = new Interpreter();

  //universal entry point
  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: jlox [script]");
    }
    else if (args.length == 1) {
      RunFile(args[0]);
    }
    else {
      RunPrompt();
    }
  }

  //wrapper functions
  private static void RunFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    Run(new String(bytes, Charset.defaultCharset()));
    if (errorState) {
      System.exit(1);
    }
  }

  private static void RunPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for (;;) {
      System.out.print(">");
      Run(reader.readLine());
      errorState = false;
    }
  }

  //core function
  private static void Run(String source) {
    Lexer lexer = new Lexer(source);
    Parser parser = new Parser(lexer.ScanTokens());

    Expr expression = parser.Parse();

    if (errorState) return;

    interpreter.Interpret(expression);
  }

  //error functions
  static void Error(int line, String msg) {
    Report(line, "", msg);
  }

  static void Error(Token token, String msg) {
    if (token.type == TokenType.EOF) {
      Report(token.line, " at end of file", msg);
    }
    else {
      Report(token.line, "at '" + token.lexeme + "'", msg);
    }
  }

  static void RuntimeError(RuntimeError error) {
    Report(error.token.line, "", error.getMessage());
  }

  private static void Report(int line, String where, String msg) {
    System.err.println("[line " + line + "] Error" + where + ": " + msg);
    errorState = true;
  }
}
