package com.moebuff.discord.listener;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moebuff.discord.maps.Maps;
import com.moebuff.discord.utils.Log;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MsgFromQQ extends Thread{

    /*
    private static ServerSocket server;
    private static StringBuilder MsgFromQQ = new StringBuilder("```[[From QQ]]: ");
    private boolean enabled = false;

    private static int laststart = 0;
    private static boolean hasImages = false;
    private static boolean hasTexts = false;

    private static MsgFromQQ instance;
    private static Thread qqThread;
    private MsgFromQQ(){

    }

    public static MsgFromQQ getInstance() {
        if(instance==null)
            instance = new MsgFromQQ();
        return instance;
    }

    public static Thread getQQThread(){
        qqThread = new Thread(getInstance());
        return qqThread;
    }

    public void setEnabled(){
        if(instance==null)
            instance = new MsgFromQQ();
        instance.enabled = true;
    }

    public void setDisnabled(){
        if(instance==null)
            instance = new MsgFromQQ();
        instance.enabled = false;
    }

    public boolean isEnabled(){
        return this.enabled;
    }

    private static IGuild guild;
    @EventSubscriber
    public static void onReady(ReadyEvent event){
        try {
            server = new ServerSocket(5200);
            guild = event.getClient().getGuildByID(267506592977649675L);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(this.enabled){
            try {
                Socket socket = server.accept();
                Log.getLogger().info("socket accepted");
                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = reader.readLine();
                Log.getLogger().info(line);
                JsonObject jsonObject = new JsonParser().parse(line).getAsJsonObject();
                //格式是这样的
                //私聊：{"font":43829384,"message":"1","message_id":227,"message_type":"private","post_type":"message","self_id":3363779021,"sub_type":"friend","time":1522310959,"user_id":410762240}
                //群聊：{"anonymous":"","anonymous_flag":"","font":144241552,"group_id":139841354,"message":"[CQ:image,file=FFEF68014AC58C66B6B8C115C11F2442.jpg,url=https://gchat.qpic.cn/gchatpic_new/410762240/2082841354-2188827334-FFEF68014AC58C66B6B8C115C11F2442/0?vuin=3363779021&amp;term=2]","message_id":147,"message_type":"group","post_type":"message","self_id":3363779021,"sub_type":"normal","time":1522052680,"user_id":410762240}
                //多图：{"anonymous":"","anonymous_flag":"","font":43957000,"group_id":139841354,"message":"测试一下[CQ:image,file=FFEF68014AC58C66B6B8C115C11F2442.jpg,url=https://gchat.qpic.cn/gchatpic_new/410762240/2082841354-2188827334-FFEF68014AC58C66B6B8C115C11F2442/0?vuin=3363779021&amp;term=2]测试测试[CQ:image,file=FFEF68014AC58C66B6B8C115C11F2442.jpg,url=https://gchat.qpic.cn/gchatpic_new/410762240/2082841354-2188827334-FFEF68014AC58C66B6B8C115C11F2442/0?vuin=3363779021&amp;term=2][CQ:image,file=FFEF68014AC58C66B6B8C115C11F2442.jpg,url=https://gchat.qpic.cn/gchatpic_new/410762240/2082841354-2188827334-FFEF68014AC58C66B6B8C115C11F2442/0?vuin=3363779021&amp;term=2]111","message_id":283,"message_type":"group","post_type":"message","self_id":3363779021,"sub_type":"normal","time":1522373253,"user_id":410762240}
                //coolq不会收到自己发的消息
                //if(jsonObject.get("user_id").getAsString().equals("3363779021"))
                //    return;

                String msg = jsonObject.get("message").getAsString();
                MsgFromQQ = new StringBuilder("**[[From QQ]]: <");
                MsgFromQQ.append(jsonObject.get("user_id").getAsString());
                MsgFromQQ.append(">:** \n");
                MsgFromQQ.append("```[Texts]:``` \n");
                String[] texts = msg.split("\\[CQ:image,\\S*?\\]");
                hasTexts = texts.length > 0;
                if(hasTexts){
                    for(int i=0;i<texts.length;i++){
                        if(!texts[i].equals("")){
                            MsgFromQQ.append("`\"");
                            MsgFromQQ.append(texts[i]);
                            MsgFromQQ.append("\"`, ");
                        }
                    }
                    MsgFromQQ.delete(MsgFromQQ.lastIndexOf(","),MsgFromQQ.length());
                }else{
                    MsgFromQQ.append("`no texts`");
                }
                MsgFromQQ.append("\n");
                MsgFromQQ.append("```[Images]:``` \n");
                String image = "";
                hasTexts = false;
                for(int i = 0;i < msg.length()-"[CQ:image,file=".length();i++){
                    if(msg.substring(i, i+"[CQ:image,file=".length()).equals("[CQ:image,file=")){
                        laststart = i;
                        hasImages = true;
                        image = msg.substring(msg.indexOf(",url=", laststart) + ",url=".length(), msg.indexOf(";term=", laststart));
                        MsgFromQQ.append(image);
                        MsgFromQQ.append("\n");
                    }
                }
                if(!hasImages)
                    MsgFromQQ.append("`no images`");
                IChannel channel =Maps.QQChannelForGuild.get(guild);
                channel.sendMessage(MsgFromQQ.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
*/
}
