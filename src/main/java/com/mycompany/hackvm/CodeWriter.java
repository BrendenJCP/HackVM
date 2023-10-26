/*

 */
package com.mycompany.hackvm;

import java.io.*;

/**
    //Initialize SP        
    @SP
    M=256

*   //Pop
    @SP
    M=M-1
    
    //Push
    @SP
    M=M+1
    A=M-1
    M=%d(index)        
 */




public class CodeWriter {
    private BufferedWriter outFile;
    private String fileName;
    private int labelNum = 1;
    
    public CodeWriter(BufferedWriter outFile){
        this.outFile = outFile;
    }
    
    public void setFileName(String fileName){
        this.fileName = fileName;
    }
    
    public void writeArithmetic(String command){
        try{
            switch(command){
                case "add" -> outFile.write("@SP\nM=M-1\nA=M\nD=M\n@SP\nA=M-1\nM=M+D\n");
                case "sub" -> outFile.write("@SP\nM=M-1\nA=M\nD=M\n@SP\nA=M-1\nM=M-D\n");
                case "neg" -> outFile.write("@SP\nA=M-1\nM=-M\n");
                case "eq" -> {
                    outFile.write(String.format("@SP\nM=M-1\nA=M\nD=M\n@SP\nA=M-1\nM=M-D\nD=M\n@false%d\nD;JNE\n@SP\nA=M-1\nM=M-1\n@next%d\n0;JMP\n(false%d)\n@0\nD=A\n@SP\nA=M-1\nM=D\n(next%d)\n", labelNum, labelNum, labelNum, labelNum));
                    labelNum++;
                }
                case "gt" -> {
                    outFile.write(String.format("@SP\nM=M-1\nA=M\nD=M\n@SP\nA=M-1\nM=M-D\nD=M\n@false%d\nD;JLE\n@0\nD=A\n@SP\nA=M-1\nM=D-1\n@next%d\n0;JMP\n(false%d)\n@0\nD=A\n@SP\nA=M-1\nM=D\n(next%d)\n", labelNum, labelNum, labelNum, labelNum));
                    labelNum++;
                }
                case "lt" -> {
                    outFile.write(String.format("@SP\nM=M-1\nA=M\nD=M\n@SP\nA=M-1\nM=M-D\nD=M\n@false%d\nD;JGE\n@0\nD=A\n@SP\nA=M-1\nM=D-1\n@next%d\n0;JMP\n(false%d)\n@0\nD=A\n@SP\nA=M-1\nM=D\n(next%d)\n", labelNum, labelNum, labelNum, labelNum));
                    labelNum++;
                }
                case "and" -> outFile.write("@SP\nM=M-1\nA=M\nD=M\n@SP\nA=M-1\nM=M&D\n");
                case "or" -> outFile.write("@SP\nM=M-1\nA=M\nD=M\n@SP\nA=M-1\nM=M|D\n");
                case "not" -> outFile.write("@SP\nA=M-1\nM=!M\n");
                default -> {}
            }
        }
        catch(IOException e){}
    }
    
    public void writePushPop(String command, String segment, int index){
        String fstring = "";
        if(command.equals("C_POP")) fstring = "@SP\nM=M-1\n";
        else{
            if(segment.equals("constant"))
                fstring  = String.format("@%d\nD=A\n@SP\nM=M+1\nA=M-1\nM=D\n", index);
        }
        try{
            outFile.write(fstring);
        }catch(IOException e){}
    }
    
    public void close(){
        try{
            outFile.close();
        }catch(IOException e){}
    }
}
