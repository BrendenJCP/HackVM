package com.mycompany.hackvm;
import java.io.*;

/**

 */
public class HackVM {

    public static void main(String[] args) {
        String path = (args.length == 0) ? "SimpleFunction.vm": args[0];
        Parser parser = new Parser(openInFile(path));
        CodeWriter codeWriter = new CodeWriter(path.split("\\.")[0]);
        
        parser.advance(); 
        while(parser.hasMoreCommands()){
            switch(parser.commandType()){
                case "C_PUSH", "C_POP" -> codeWriter.writePushPop(parser.commandType(), parser.arg1(), Integer.parseInt(parser.arg2()));
                case "C_ARITHMETIC" -> codeWriter.writeArithmetic(parser.arg1());
                case "C_FUNCTION" -> codeWriter.writeFunction(parser.arg1(), Integer.parseInt(parser.arg2()));
                case "C_RETURN" -> codeWriter.writeReturn();
                case "C_CALL" -> codeWriter.writeCall(parser.arg1(), Integer.parseInt(parser.arg2()));
                case "C_IF" -> codeWriter.writeIf(parser.arg1());
                case "C_GOTO" -> codeWriter.writeGoto(parser.arg1());
                case "C_LABEL" -> codeWriter.writeLabel(parser.arg1());
                default -> {}
            }
            parser.advance();
        }
        codeWriter.close();
    }
    
    public static BufferedReader openInFile(String path){
        try{
            return new BufferedReader(new FileReader(path));
        }catch(IOException e){}
        return null;
    }
    
    public static BufferedWriter openOutFile(String path){
        try{
            return new BufferedWriter(new FileWriter(path));
        }catch(IOException e){}
        return null;
    }
}
