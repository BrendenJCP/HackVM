/*

 */
package com.mycompany.hackvm;

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
    public CodeWriter(){}
    
    public void setFileName(String fileName){
        
    }
    
    public void writeArithmetic(String command){
        
    }
    
    public void writePushPop(String command, String segment, int index){
        String fstring;
        if(command.equals("C_PUSH")){
            fstring  = String.format("@SP\nM=M+1\nA=M-1\nM=%d\n", index);
        }
        else{
            fstring = "@SP\nM=M-1\n";
        }
        System.out.println(fstring);
    }
    
    public void Close(){
    
    }
}
