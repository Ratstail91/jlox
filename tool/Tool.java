package com.krgamestudios.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class Tool {
  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      System.err.println("Usage: tool OP [outfile]");
      System.exit(1);
    }

    //choose the correct mode
    if (args[0].equals("Expr")) {
      DefineAst(args[1], "Expr", Arrays.asList(
        "Assign   : Token name,Expr value",
        "Binary   : Expr lhs,Token operator,Expr rhs",
        "Grouping : Expr expression",
        "Literal  : Object value",
        "Unary    : Token operator,Expr rhs",
        "Variable : Token name"
      ));
    }
    else if (args[0].equals("Stmt")) {
      DefineAst(args[1], "Stmt", Arrays.asList(
        "Block      : List<Stmt> statements",
        "Expression : Expr expression",
        "Print      : Expr expression",
        "Var        : Token name,Expr initializer"
      ));
    }
    else {
      System.err.println("NO OP");
    }
  }

  private static void DefineAst(String outDir, String baseName, List<String> types) throws IOException {
    //open the writer
    String path = outDir + "/" + baseName + ".java";
    PrintWriter writer = new PrintWriter(path, "UTF-8");

    //write the basics
    writer.println("package com.krgamestudios.lox;");
    writer.println("");
    writer.println("import java.util.List;");
    writer.println("");
    writer.println("abstract class " + baseName + " {");

    DefineASTVisitor(writer, baseName, types);

    //write each AST class
    for (String type : types) {
      String className = type.split(":")[0].trim();
      String fields = type.split(":")[1].trim();
      DefineASTType(writer, baseName, className, fields);
    }

    writer.println("}");
    writer.close();
  }

  private static void DefineASTVisitor(PrintWriter writer, String baseName, List<String> types) {
    //create a visitor for each type
    writer.println("  interface Visitor<R> {");
    for (String type : types) {
      String typeName = type.split(":")[0].trim();
      writer.println("    R Visit(" + typeName + " " + baseName.toLowerCase() + ");");
    }
    writer.println("  }");

    //create the abstract accept method for the visitor pattern
    writer.println("");
    writer.println("  abstract <R> R Accept(Visitor<R> visitor);");
  }

  private static void DefineASTType(PrintWriter writer, String baseName, String className, String fieldList) {
    //store parameters in fields
    String[] fields = fieldList.split(",");

    //open
    writer.println("");
    writer.println("  static class " + className + " extends " + baseName + " {");

    //fields themselves
    for (String field : fields) {
      writer.println("    final " + field + ";");
    }
    writer.println("");

    //constructor
    writer.println("    " + className + "(" + fieldList + ") {");
    for(String field : fields) {
      String name = field.split(" ")[1];
      writer.println("      this." + name + " = " + name + ";");
    }
    writer.println("    }");

    //define the accept method
    writer.println("");
    writer.println("    <R> R Accept(Visitor<R> visitor) {");
    writer.println("      return visitor.Visit(this);");
    writer.println("    }");

    //close
    writer.println("  }");
  }
}
