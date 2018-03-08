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

        FileReader reader = new FileReader(f);
        char[] c = new char[1024];
        int[][] b = new int[20][20];
        int length = reader.read(c);
       // System.out.print(length);
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

        for(int i=0;i<b.length;i++){
            for(int j=0;j<b[i].length;j++){
                System.out.print(b[i][j]);
                System.out.print(" ");
            }
            System.out.print("\n");
        }
    }
}
