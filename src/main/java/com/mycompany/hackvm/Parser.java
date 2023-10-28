package com.mycompany.hackvm;
import java.io.*;
import java.util.Arrays;


public class Parser {
    String command;
    BufferedReader programFile;
    int lineNum;
    String[] components;
    
    public Parser(BufferedReader programFile){
        this.programFile = programFile;
        this.command = null;
    }
    
    public Boolean hasMoreCommands(){
        return command != null;
    }
    
    public Boolean skipLine(){
        if(command.length()==0) return true;
        return command.charAt(0) == '/' && command.charAt(1) == '/';
    }

    public void advance() {
        try {
            command = programFile.readLine();
            lineNum++;
            while (hasMoreCommands() && skipLine()) {
                command = programFile.readLine();
                lineNum++;
            }
            if(hasMoreCommands()){
                command = command.split("//")[0];
                components = command.split("\\s+");
            }
        } catch (IOException e) {
        }
    }
    
    public String commandType() {
        return switch(components[0]){
            case "push" -> "C_PUSH";
            case "pop" -> "C_POP";
            case "label" -> "C_LABEL";
            case "if-goto" -> "C_IF";
            case "goto" -> "C_GOTO";
            case "function" -> "C_FUNCTION";
            case "return" -> "C_RETURN";
            case "call" -> "C_CALL";
            case "add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not" -> "C_ARITHMETIC";
            default -> "C_INVALID";
        };
    }
    
    public String arg1(){
        if(commandType().equals("C_ARITHMETIC")) return components[0];
        else return components[1];    
    }
    
    public String arg2(){
        return switch(commandType()){
            case "C_PUSH", "C_POP", "C_FUNCTION", "C_CALL" -> components[2];
            default -> null;
        };
    }
}
