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
    LexicalScanner scanner = new LexicalScanner(source);
    List<Token> tokens = scanner.ScanTokens();

    //for now, just print the tokens
    for (Token token : tokens) {
      System.out.println(token);
    }
  }

  //error functions
  static void Error(int line, String message) {
    Report(line, "", message);
  }

  private static void Report(int line, String where, String message) {
    System.err.println("[line " + line + "] Error " + where + ": " + message);
    errorState = true;
  }
}
