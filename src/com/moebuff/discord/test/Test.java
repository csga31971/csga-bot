package com.moebuff.discord.test;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class Test {
    public static void main(String[] args) throws IOException {
        int[][] a = new int[20][20];
        for(int i =0;i<a.length;i++){
            Arrays.fill(a[i],1);
        }
        for(int i =0;i<a.length;i++){
            for(int j=0;j<a[i].length;j++){
                a[i][j]= new Random().nextInt(10);
            }
        }
        File f = new File("1.txt");
        FileWriter writer = new FileWriter(f);
        for(int i=0;i<a.length;i++){
            for(int j=0;j<a[i].length;j++){
                writer.write(a[i][j]+48);
                writer.write(" ");
            }
            writer.write(10);
        }
        writer.flush();
        writer.close();

        FileReader reader = new FileReader(f);
        char[] c = new char[1024];
        int[][] b = new int[20][20];
        int length = reader.read(c);
        System.out.println(length);
        String str = new String(c);
        String[] rows = str.split("\n");
        //System.out.print(rows.length);
        for(int i =0;i<rows.length;i++){
            System.out.print(rows[i]);
            System.out.print("\n");
        }
        for(int i=0;i<rows.length-1;i++){
            String[] columns = rows[i].split(" ");
            for(int j=0;j<columns.length;j++){
                b[i][j]=Integer.valueOf(columns[j]);
            }
        }

        System.out.print("******************************************************\n");

        FileWriter writer2 = new FileWriter(f);
        for(int i=0;i<a.length;i++){
            for(int j=0;j<a[i].length;j++){
                writer2.write(new Random(47).nextInt(10)+48);
                writer2.write(" ");
            }
            writer2.write(10);
        }
        writer2.flush();
        writer2.close();

        FileReader reader2 = new FileReader(f);
        char[] c2 = new char[1024];
        int[][] b2 = new int[20][20];
        int length2 = reader2.read(c2);
         System.out.println(length2);
        String str2 = new String(c);
        String[] rows2 = str2.split("\n");
        //System.out.print(rows.length);
        for(int i =0;i<rows2.length;i++){
            System.out.print(rows2[i]);
            System.out.print("\n");
        }
        for(int i=0;i<rows2.length-1;i++){
            String[] columns2 = rows2[i].split(" ");
            for(int j=0;j<columns2.length;j++){
                b2[i][j]=Integer.valueOf(columns2[j]);
            }
        }
    }
}
