package com.moebuff.discord.listener;

import com.moebuff.discord.Settings;
import com.moebuff.discord.utils.Log;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildEmojisUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.List;

public class RandomListeners {

    @EventSubscriber
    public static void onThinkingReceive(MessageReceivedEvent event){
        IMessage message = event.getMessage();
        String content = message.getContent();
        if(content.toLowerCase().contains("thinking") || content.toLowerCase().contains("thonking")){
            message.addReaction(EmojiManager.getForAlias("thinking"));
        }
    }

    @EventSubscriber
    public static void onReactionAdd(ReactionAddEvent event){
        if(event.getUser().getStringID() != Settings.BOT_ID_STRING){
            IMessage message = event.getMessage();
            IReaction reaction = event.getReaction();
            message.addReaction(reaction.getEmoji());
        }
    }

    @EventSubscriber
    public static void onMessageEdit(MessageUpdateEvent event){
        IMessage message = event.getNewMessage();
        message.addReaction(EmojiManager.getForAlias("thinking"));
    }

    @EventSubscriber
    public static void onGuildEmojisUpdate(GuildEmojisUpdateEvent event){
        IChannel channel = event.getGuild().getChannelsByName("general").get(0);
        List<IEmoji> emojis = event.getNewEmojis();
        if(channel!=null){
            for(IEmoji emoji : emojis){
                channel.sendMessage(emoji.getName());
            }
        }
    }

    @EventSubscriber
    public static void onBot(MessageReceivedEvent event){
        IMessage message = event.getMessage();
        String content = message.getContent().toLowerCase();
        if(content.contains("bot") || content.contains("机器人")){
            message.addReaction(EmojiManager.getForAlias("thinking"));
        }
    }
}
