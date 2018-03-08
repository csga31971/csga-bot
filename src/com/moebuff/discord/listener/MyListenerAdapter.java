package com.moebuff.discord.listener;

import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;

public abstract class MyListenerAdapter implements Listener {
    public MyListenerAdapter(){

    }

    public void onEvent(Event event) throws Exception{
        if(event instanceof MessageEvent){
            this.onMessage((MessageEvent)event);
        }
    }

    public void onMessage(MessageEvent event) throws IOException, InterruptedException {

    }
}
