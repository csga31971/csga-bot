package com.moebuff.discord.listener;

import org.pircbotx.dcc.SendChat;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import java.io.IOException;

public class IRCListener extends ListenerAdapter {

    @Override
    public void onPrivateMessage(PrivateMessageEvent event) throws IOException, InterruptedException {
        String message = event.getMessage();
        IRC.iChannel.sendMessage(message);
        //SendChat chat = event.getUser().send().dccChat();
    }
}
