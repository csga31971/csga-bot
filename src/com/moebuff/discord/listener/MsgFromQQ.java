package com.moebuff.discord.listener;

import com.google.gson.JsonParser;
import com.moebuff.discord.maps.Maps;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class MsgFromQQ {

    private static Socket socket;
    private static String MsgFromQQ = "";
    @EventSubscriber
    public static void onReady(ReadyEvent event){
        try {
            socket = new Socket("127.0.0.1", 55533);
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            //{"anonymous":"","anonymous_flag":"","font":144241552,"group_id":139841354,"message":"[CQ:image,file=FFEF68014AC58C66B6B8C115C11F2442.jpg,url=https://gchat.qpic.cn/gchatpic_new/410762240/2082841354-2188827334-FFEF68014AC58C66B6B8C115C11F2442/0?vuin=3363779021&amp;term=2]","message_id":147,"message_type":"group","post_type":"message","self_id":3363779021,"sub_type":"normal","time":1522052680,"user_id":410762240}
            MsgFromQQ = new JsonParser().parse(reader.readLine()).getAsJsonObject().get("message").getAsString();

            IGuild guild = event.getClient().getGuildByID(267506592977649675l);
            IChannel channel = Maps.QQChannelForGuild.get(guild);
            channel.sendMessage(MsgFromQQ);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
