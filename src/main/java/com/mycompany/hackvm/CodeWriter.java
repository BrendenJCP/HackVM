package com.mycompany.hackvm;
import java.io.*;

public class CodeWriter {
    private BufferedWriter outFile;
    private String fileName;
    private int labelNum = 1;
    private int returnNum = 1;
    private String currFuncName;
    private String pushD = "@SP\nM=M+1\nA=M-1\nM=D\n";
    
    public CodeWriter(String name){
        this.outFile = openOutFile(name+".asm");
        this.fileName = name;
        currFuncName = null;
    }
    
    public void setFileName(String fileName){
        this.fileName = fileName;
    }
    
    public void writeInit(){
        String fstring ="";
        fstring += "@256\nD=A\n@SP\nM=D\n";
        try{
            outFile.write(fstring);
        }catch(IOException e){}
        writeCall("Sys.init", 0);
    }
    
    public void writeLabel(String label){
        //Add code to append function name to label
        if(currFuncName != null)
            label = currFuncName+"$"+label;
        String fstring = String.format("(%s)\n",label);
        try{
            outFile.write(fstring);
        }catch(IOException e){}
    }
    
    public void writeGoto(String label){
        if(currFuncName != null)
            label = currFuncName+"$"+label;
        String fstring = String.format("@%s\n0;JMP\n",label);
        try{
            outFile.write(fstring);
        }catch(IOException e){}
    }
    
    public void writeIf(String label){
        if(currFuncName != null)
            label = currFuncName+"$"+label;
        String fstring = String.format("@SP\nM=M-1\nA=M\nD=M\n@%s\nD;JNE\n",label);
        try{
            outFile.write(fstring);
        }catch(IOException e){}
    }
    
    public void writeCall(String functionName, int numArgs){
        String fstring = "";
        fstring += String.format("@return.%s%d\nD=A\n", functionName, returnNum);
        fstring += pushD;
        fstring += "@LCL\nD=M\n" + pushD + "@ARG\nD=M\n" + pushD +"@THIS\nD=M\n" + pushD +"@THAT\nD=M\n" + pushD;
        fstring += String.format("@%d\nD=A\n@SP\nD=M-D\n@ARG\nM=D\n", numArgs+5);
        fstring += "@SP\nD=M\n@LCL\nM=D\n";
        fstring += String.format("@%s\n0;JMP\n", functionName);
        fstring += String.format("(return.%s%d)\n", functionName, returnNum);
        try{
            outFile.write(fstring);
        }catch(IOException e){}
        returnNum++;
    }
    
    public void writeReturn(){
        String fstring = "";
        fstring += "@LCL\nD=M\n@5\nM=D\n";
        fstring += "@5\nD=A\nA=M-D\nD=M\n@6\nM=D\n";
        fstring += "@SP\nM=M-1\nA=M\nD=M\n@ARG\nA=M\nM=D\n";
        fstring += "@ARG\nD=M+1\n@SP\nM=D\n";
        fstring += "@5\nA=M-1\nD=M\n@THAT\nM=D\n";
        fstring += "@5\nD=M\n@2\nA=D-A\nD=M\n@THIS\nM=D\n";
        fstring += "@5\nD=M\n@3\nA=D-A\nD=M\n@ARG\nM=D\n";
        fstring += "@5\nD=M\n@4\nA=D-A\nD=M\n@LCL\nM=D\n";
        fstring += "@6\nA=M\n0;JMP\n";
        
        try{
            outFile.write(fstring);
        }catch(IOException e){}
//        currFuncName = null;
    }
    
    public void writeFunction(String functionName, int numLocals){
        currFuncName = functionName;
        String fstring = String.format("(%s)\n",functionName);
        try{
            outFile.write(fstring);
        }catch(IOException e){}
        for(int i = 0; i < numLocals; i++){
            writePushPop("C_PUSH", "constant", 0);
            writePushPop("C_POP", "local", i);
        }
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
