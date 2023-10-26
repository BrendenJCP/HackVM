/*
 
 */

package com.mycompany.hackvm;
import java.io.*;
import java.util.*;

/**

 */
public class HackVM {

    public static void main(String[] args) {
        String path = (args.length == 0) ? "SimpleAdd.vm": args[0];
        Parser parser = new Parser(openFile(path));
        CodeWriter codeWriter = new CodeWriter();
        
        parser.advance(); 
        while(parser.hasMoreCommands()){
            if(parser.commandType().equals("C_PUSH") || parser.commandType().equals("C_POP"))
                codeWriter.writePushPop(parser.commandType(), parser.arg1(), Integer.parseInt(parser.arg2()));
            if(parser.commandType().equals("C_ARITHMETIC"))
                codeWriter.writeArithmetic(parser.arg1());
            parser.advance();
        }
    }
    
    public static BufferedReader openFile(String path){
        try{
            return new BufferedReader(new FileReader(path));
        }catch(IOException e){}
        return null;
    }
}
