package com.moebuff.discord.listener;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * OSU Support
 */
public class OhShitUninstall {
    static void osu(IChannel channel)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        Audio.queueFile(channel, "西野カナ - Story - 变调版.mp3");
        Audio.queueFile(channel, "300034 YooSanHyakurei - Ketsubetsu no Tabi/ml.mp3");
        Audio.queueFile(channel, "finish.wav");
    }
}
