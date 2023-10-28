package com.mycompany.hackvm;
import java.io.*;

public class HackVM {
    public static CodeWriter codeWriter;

    public static void main(String[] args) {
        String path = (args.length == 0) ? "FibonacciElement": args[0];
        codeWriter = new CodeWriter(path.split("\\.")[0]);
        if(path.split("\\.").length<=1){
            codeWriter.writeInit();
            File dir = new File(path);
            File[] directory = dir.listFiles();
            for(File file: directory){
                if(file.getName().split("\\.")[1].equals("vm")){
                    codeWriter.setFileName(file.getName().split("\\.")[0]);
                    translateFile(path+"/"+file.getName());
                }
            }
        }
        else
            translateFile(path);
        codeWriter.close();
    }
    
    public static void translateFile(String path){
        Parser parser = new Parser(openInFile(path));
        
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
