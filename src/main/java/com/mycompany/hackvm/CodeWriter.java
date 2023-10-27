package com.mycompany.hackvm;
import java.io.*;

public class CodeWriter {
    private BufferedWriter outFile;
    private String fileName;
    private int labelNum = 1;
    private int varNum = 1;
    
    public CodeWriter(String file){
        this.outFile = openOutFile(file+".asm");
        this.fileName = file;
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
                case "and" ->
                    outFile.write("@SP\nM=M-1\nA=M\nD=M\n@SP\nA=M-1\nM=M&D\n");
                case "or" ->
                    outFile.write("@SP\nM=M-1\nA=M\nD=M\n@SP\nA=M-1\nM=M|D\n");
                case "not" ->
                    outFile.write("@SP\nA=M-1\nM=!M\n");
                default -> {
                }
            }
        } catch (IOException e) {
        }
    }

    public void writePushPop(String command, String segment, int index) {
        String fstring = "";
        String seg = "";
        
        switch (segment) {
            case "this" -> seg = "THIS";
            case "that" -> seg = "THAT";
            case "local" -> seg = "LCL";
            case "argument" -> seg = "ARG";
            case "temp" -> seg = "5";
            case "pointer" -> {
                if(index == 0) seg = "THIS";
                if(index == 1) seg = "THAT";
                index = 0;
            }
            default -> {
            }
        }
        if (command.equals("C_PUSH")) {
            if (segment.equals("constant")) {
                fstring = String.format("@%d\nD=A\n@SP\nM=M+1\nA=M-1\nM=D\n", index);
            } 
            else if(segment.equals("pointer") || segment.equals("temp")){
                fstring = String.format("@%s\nD=A\n@%d\nA=D+A\nD=M\n@SP\nM=M+1\nA=M-1\nM=D\n", seg, index);
            }
            else if(segment.equals("static")){
                fstring = String.format("@%s.%d\nD=M\n@SP\nM=M+1\nA=M-1\nM=D\n", fileName, index);
            }
            else {
                fstring = String.format("@%s\nD=M\n@%d\nA=D+A\nD=M\n@SP\nM=M+1\nA=M-1\nM=D\n", seg, index);
            }
        }
        else{
            if(segment.equals("pointer") || segment.equals("temp"))
                fstring = String.format("@SP\nM=M-1\n@%s\nD=A\n@%d\nA=A+D\nD=A\n@R13\nM=D\n@SP\nA=M\nD=M\n@R13\nA=M\nM=D\n", seg, index);
            else if (segment.equals("static"))
                fstring = String.format("@SP\nM=M-1\n@%s.%d\nD=A\n@R13\nM=D\n@SP\nA=M\nD=M\n@R13\nA=M\nM=D\n", fileName, index);
            else
                fstring = String.format("@SP\nM=M-1\n@%s\nD=M\n@%d\nA=A+D\nD=A\n@R13\nM=D\n@SP\nA=M\nD=M\n@R13\nA=M\nM=D\n", seg, index);
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
    
    public static BufferedWriter openOutFile(String path){
        try{
            return new BufferedWriter(new FileWriter(path));
        }catch(IOException e){}
        return null;
    }
}
