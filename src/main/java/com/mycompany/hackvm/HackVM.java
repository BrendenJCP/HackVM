package com.mycompany.hackvm;
import java.io.*;

/**

 */
public class HackVM {

    public static void main(String[] args) {
        String path = (args.length == 0) ? "StaticTest.vm": args[0];
        Parser parser = new Parser(openInFile(path));
        CodeWriter codeWriter = new CodeWriter(path.split("\\.")[0]);
        
        parser.advance(); 
        while(parser.hasMoreCommands()){
            if(parser.commandType().equals("C_PUSH") || parser.commandType().equals("C_POP"))
                codeWriter.writePushPop(parser.commandType(), parser.arg1(), Integer.parseInt(parser.arg2()));
            if(parser.commandType().equals("C_ARITHMETIC"))
                codeWriter.writeArithmetic(parser.arg1());
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
