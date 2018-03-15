package com.moebuff.discord.listener;

import com.moebuff.discord.utils.FileUtil;
import com.moebuff.discord.utils.Log;
import sun.font.FontDesignMetrics;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

public class PandaHandler {

    private static final String TOO_LONG = "your param is too long";

    public static void handle(IGuild guild, IChannel channel, IUser sender, IMessage message, String[] args) {
        if (args.length == 0) {
            channel.sendMessage("The command requires some additional parameters.");
            channel.sendMessage("For details, refer to the help documentation.");
            return;
        }
        boolean prompt = false;//是否需要提示
        String[] params = args.length > 1 ?
                Arrays.copyOfRange(args, 1, args.length) :
                new String[0];
        String param_with_spacebar = "";

        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                param_with_spacebar += args[i];
                param_with_spacebar += " ";
            }
        }
        //怎么判断比较好
        try{
            int index = Integer.valueOf(args[0]);
        } catch (Exception e){
            channel.sendMessage("wrong params");
        }
        int index = Integer.valueOf(args[0]);
        panda(channel, index, param_with_spacebar);
    }

    private static void panda(IChannel channel,int index, String text){
        if(index < 1 || index > 3){
            channel.sendMessage("only support 1,2,3 now");
            return;
        }

        //空字符串
        if(text.matches("\\s*")){
            File file = new File("panda/panda" + index + ".jpg");
            File file_result = new File("panda/panda_result.jpg");
            FileUtil.copyFile(file,file_result);

            ImageIcon imageIcon = new ImageIcon("panda/" + file.getName());
            Image image = imageIcon.getImage();
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            BufferedImage bufferedImage = new BufferedImage(width, height-30, BufferedImage.TYPE_INT_RGB);

            Graphics2D g = bufferedImage.createGraphics();
            g.drawImage(image,0,0,null);
            try {
                FileOutputStream outputStream2 = new FileOutputStream(file_result);
                ImageIO.write(bufferedImage, "jpg", outputStream2);
                outputStream2.close();
                channel.sendFile(file_result);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                //复制一份模板
                File file = new File("panda/panda" + index + ".jpg");
                File file_result = new File("panda/panda_result.jpg");
                FileUtil.copyFile(file,file_result);

                ImageIcon imageIcon = new ImageIcon("panda/" + file.getName());
                Image image = imageIcon.getImage();
                int width = image.getWidth(null);
                int height = image.getHeight(null);
                BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                Graphics2D g = bufferedImage.createGraphics();
                g.setColor(Color.BLACK);
                g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
                g.drawImage(image,0,0,null);
                int fontSize = 35;
                Font font = new Font("黑体",Font.PLAIN,fontSize);
                FontMetrics fontMetrics = FontDesignMetrics.getMetrics(font);
                g.setFont(font);

                String s = text;
                int strWidth = fontMetrics.stringWidth(s);
                Log.getLogger().debug(s + ": " + strWidth);
                if(strWidth > 160){
                    if(strWidth > 450){
                        s = TOO_LONG;
                        fontSize = 14;
                        font = new Font(null,Font.BOLD,fontSize);
                        g.setFont(font);

                        //这两句没用，为了log
                        fontMetrics = FontDesignMetrics.getMetrics(font);
                        strWidth = fontMetrics.stringWidth(s);
                        Log.getLogger().debug("too long: " + fontSize + " - " + s + " - " + strWidth);
                    }else{
                        fontSize = fontSize*160/strWidth;
                        font = new Font(null,Font.BOLD,fontSize);
                        g.setFont(font);

                        //这两句没用，为了log
                        fontMetrics = FontDesignMetrics.getMetrics(font);
                        strWidth = fontMetrics.stringWidth(s);
                        Log.getLogger().debug("modified: " + fontSize + " - " + s + " - " + strWidth);
                    }
                }
                g.drawString(s,width/2 - strWidth/2,150+fontSize/2);
                g.dispose();
                FileOutputStream outputStream2 = new FileOutputStream(file_result);
                ImageIO.write(bufferedImage, "jpg", outputStream2);
                outputStream2.close();
                channel.sendFile(file_result);
                //file_result.delete();
            } catch (Exception e){
                e.printStackTrace();
                channel.sendMessage("something is wrong");
            }
        }

    }
}
