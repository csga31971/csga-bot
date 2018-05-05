package com.moebuff.discord.maps;

import com.moebuff.discord.utils.oppai.Koohii;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.util.HashMap;
import java.util.Map;

public class Maps {
    public static Map<IGuild, IChannel> QQChannelForGuild = new HashMap<>();
    public static Map<IChannel, Koohii.Map> LastBeatMapRequested = new HashMap<>();
}
