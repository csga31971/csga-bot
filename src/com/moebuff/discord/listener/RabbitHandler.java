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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;

public class RabbitHandler {

    private static final String TOO_LONG_TOP = "参数太长";
    private static final String TOO_LONG_BOTTOM = "发出参数太长的声音";
    private static final File EXCETION = new File("res/rabbit/exception.png");
    private static File file = new File("res/rabbit/rabbit.jpg");
    private static File file_result = new File("res/rabbit/rabbit_result.jpg");


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

        if(args.length < 2){
            rabbit(channel, "", "");
        }else{
            rabbit(channel, args[0], args[1]);
        }

    }

    private static void rabbit(IChannel channel, String textTop, String testBottom){
        try {
            FileUtil.copyFile(file,file_result);

            ImageIcon imageIcon = new ImageIcon("res/rabbit/" + file_result.getName());
            Image image = imageIcon.getImage();
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            Graphics2D g = bufferedImage.createGraphics();
            g.setColor(Color.BLACK);
            g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
            g.drawImage(image,0,0,null);

            int fontSizeTop = 20;
            int fontSizeBottom = 35;
            Font fontTop = new Font("黑体",Font.PLAIN,fontSizeTop);
            Font fontBottom = new Font("黑体",Font.PLAIN,fontSizeBottom);
            FontMetrics fontTopMetrics = FontDesignMetrics.getMetrics(fontTop);
            FontMetrics fontBottomMetrics = FontDesignMetrics.getMetrics(fontBottom);
            String top = textTop;
            String bottom = "发出" + testBottom + "的声音";
            int strWidthTop = fontTopMetrics.stringWidth(top);
            int strWidthBottom = fontBottomMetrics.stringWidth(bottom);
            Log.getLogger().debug(strWidthTop + ": " + top + " & " + strWidthBottom + ": " + bottom);

            //绘制对话框文字
            g.setFont(fontTop);
            if(strWidthTop > 50){
                if(strWidthTop > 80){
                    top = TOO_LONG_TOP;
                    fontSizeTop = 14;
                    fontTop = new Font(null,Font.BOLD,fontSizeTop);
                    g.setFont(fontTop);

                    //这两句没用，为了log
                    fontTopMetrics = FontDesignMetrics.getMetrics(fontTop);
                    strWidthTop = fontTopMetrics.stringWidth(top);
                    Log.getLogger().debug("top too long: " + fontSizeTop + " - " + top + " - " + strWidthTop);
                }else{
                    fontSizeTop = fontSizeTop*60/strWidthTop;
                    fontTop = new Font(null,Font.BOLD,fontSizeTop);
                    g.setFont(fontTop);

                    //这两句没用，为了log
                    fontTopMetrics = FontDesignMetrics.getMetrics(fontTop);
                    strWidthTop = fontTopMetrics.stringWidth(top);
                    Log.getLogger().debug("top too long: " + fontSizeTop + " - " + top + " - " + strWidthTop);
                }
            }
            g.drawString(top,35 - strWidthTop/2,20+fontSizeTop/2);

            //绘制底部文字
            if(strWidthBottom > 140){
                if(strWidthBottom > 400){
                    bottom = TOO_LONG_BOTTOM;
                    fontSizeBottom = 14;
                    fontBottom = new Font(null,Font.BOLD,fontSizeBottom);
                    g.setFont(fontBottom);

                    //这两句没用，为了log
                    fontBottomMetrics = FontDesignMetrics.getMetrics(fontBottom);
                    strWidthBottom = fontBottomMetrics.stringWidth(bottom);
                    Log.getLogger().debug("top too long: " + fontSizeBottom + " - " + bottom + " - " + strWidthBottom);
                }else{
                    fontSizeBottom = fontSizeBottom*140/strWidthBottom;
                    fontBottom = new Font(null,Font.BOLD,fontSizeBottom);
                    g.setFont(fontBottom);

                    //这两句没用，为了log
                    fontBottomMetrics = FontDesignMetrics.getMetrics(fontBottom);
                    strWidthBottom = fontBottomMetrics.stringWidth(bottom);
                    Log.getLogger().debug("bottom too long: " + fontSizeBottom + " - " + bottom + " - " + strWidthBottom);
                }
            }
            g.drawString(bottom,width/2 - strWidthBottom/2,135+fontSizeBottom/2);
            g.dispose();
            FileOutputStream outputStream2 = new FileOutputStream(file_result);
            ImageIO.write(bufferedImage, "jpg", outputStream2);
            outputStream2.close();
            channel.sendFile(file_result);
            //file_result.delete();
        } catch (Exception e){
            e.printStackTrace();
            try {
                channel.sendFile(EXCETION);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            channel.sendMessage("something is wrong:" + e.getMessage());
        }
    }
}
