package com.example.familychat.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileOperation {
    private FileOperation(){

    }
    public static void writeIntoFile(File path,String fileName,String content){
        try{
            FileOutputStream write = new FileOutputStream(new File(path,fileName));
            write.write(content.getBytes());
            write.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public static String readFromFile(File path,String fileName){
        try{
            File readFrom = new File(path,fileName);
            byte[] content = new byte[(int) readFrom.length()];
            FileInputStream read = new FileInputStream(readFrom);
            read.read(content);
            return  new String(content);
        }catch (Exception e){
            return "NotFound";
        }
    }
    public static boolean deleteFile(File path,String fileName){
        File file = new File(path,fileName);
        return file.delete();
    }
}
