package com.moebuff.discord.utils;

import java.io.*;

public class FileUtil {
    public static void copyFile(File from, File to){
        try{
            FileInputStream inputStream = new FileInputStream(from);
            FileOutputStream outputStream = new FileOutputStream(to);
            byte[] b = new byte[1024];
            int n=0;
            while((n=inputStream.read(b))!=-1){
                outputStream.write(b, 0, n);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void copyFile(String from, String to){
        try{
            FileInputStream inputStream = new FileInputStream(from);
            FileOutputStream outputStream = new FileOutputStream(to);
            byte[] b = new byte[1024];
            int n=0;
            while((n=inputStream.read(b))!=-1){
                outputStream.write(b, 0, n);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
