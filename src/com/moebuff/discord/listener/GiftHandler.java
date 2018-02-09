package com.moebuff.discord.listener;

import com.moebuff.discord.entity.Gift;
import com.moebuff.discord.entity.User;
import com.moebuff.discord.service.GiftManager;
import com.moebuff.discord.service.UserManager;
import com.moebuff.discord.utils.Log;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GiftHandler {

    private static List<IUser> mentionedList = new ArrayList<IUser>();

    public static void handle(IGuild guild,IChannel channel,IUser sender, IMessage message, String[] args){
        initUser(guild);
        if (args.length == 0) {
            channel.sendMessage("The command requires some additional parameters.");
            channel.sendMessage("For details, refer to the help documentation.");
            return;
        }
        mentionedList = message.getMentions();
        boolean prompt = false;//是否需要提示
        String[] params = args.length > 1 ?
                Arrays.copyOfRange(args, 1, args.length) :
                new String[0];
        String param_with_spacebar = "";

        if(args.length>1){
            for(int i = 1;i < args.length;i++){
                param_with_spacebar += args[i];
                param_with_spacebar += " ";
            }
        }
        switch (args[0]){
            case "give":
            case "g":
                int index = 0;
                if(!params[0].startsWith("<@")){
                    channel.sendMessage("please mention the ones you want to send gift");
                    return;
                }
                while(params[index].startsWith("<@")){
                    index++;
                }
                Gift g = GiftManager.getGift(Integer.valueOf(params[index]));
                if(g==null){
                    channel.sendMessage("index out of range, please check available gift list!\n");
                    showGiftList(channel);
                    return;
                }
                sendGift(channel, sender, mentionedList, g);
                break;
            case "list":
            case "l":
                showGiftList(channel);
                break;
            case "info":
                if(mentionedList == null || mentionedList.isEmpty())
                    showInfo(channel, sender);
                else
                    showInfo(channel, mentionedList);
                break;
             default:
                 channel.sendMessage("unknown command");
                 break;

        }
    }

    private static void initUser(IGuild guild){
        List<IUser> userList = guild.getUsers();
        for(IUser iUser:userList){
            User user = UserManager.getUser(iUser.getStringID());
            if(user == null){
                UserManager.addUser(iUser);
            }
        }
    }

    private static void sendGift(IChannel channel, IUser sender, List<IUser> receiverListlist, Gift g){
        for(IUser receiver:receiverListlist){
            String msg = GiftManager.sendGift(sender, receiver, g);
            if(msg.startsWith("not enough money, you have $")){
                channel.sendMessage(msg);
                return;
            }
            for(IUser iUser: receiverListlist){
                msg += iUser.getName();
                msg += " & ";
            }
            msg = msg.substring(0,msg.lastIndexOf("&"));
            msg += ", each receiver will gain ";
            msg += g.getCharm();
            msg += " charm points.";
            channel.sendMessage(msg);
        }
    }

    private static void showGiftList(IChannel channel){
        List<Gift> giftList = GiftManager.getGiftList();
        String msg = "";
        int count = 0;
        for(Gift gift:giftList){
            count++;
            msg += count;
            msg += ". ";
            msg += gift.toString();
            msg += "\n";
        }
        channel.sendMessage(msg);
    }

    private static void showInfo(IChannel channel, IUser iUser){
        channel.sendMessage(UserManager.getInfo(iUser));
    }

    private static void showInfo(IChannel channel, List<IUser> mentionedList){
        String msg = "";
        for(IUser iUser:mentionedList){
            msg += UserManager.getInfo(iUser);
            msg += "\n";
        }
        channel.sendMessage(msg);
    }
}
