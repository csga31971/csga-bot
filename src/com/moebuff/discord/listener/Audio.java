package com.moebuff.discord.listener;

import com.moebuff.discord.io.FF;
import com.moebuff.discord.io.FileHandle;
import com.moebuff.discord.utils.Log;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.events.TrackFinishEvent;
import sx.blah.discord.util.audio.events.TrackQueueEvent;
import sx.blah.discord.util.audio.events.TrackStartEvent;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 音频指令
 *
 * @author muto
 */
public class Audio {
    // Stores the last channel that the join command was sent from
    private static final Map<IGuild, IChannel> LAST_CHANNEL = new HashMap<>();
    private static final Map<IGuild, IVoiceChannel> LAST_VOICE = new HashMap<>();

    private static final String CHROME
            = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/55.0.2883.87 Safari/537.36";

    @EventSubscriber
    public static void onTrackQueue(TrackQueueEvent event)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        IGuild guild = event.getPlayer().getGuild();
        String msg = String.format("Added **%s** to the playlist.", getTrackTitle(event));
        LAST_CHANNEL.get(guild).sendMessage(msg);
    }

    @EventSubscriber
    public static void onTrackStart(TrackStartEvent event)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        IGuild guild = event.getPlayer().getGuild();
        String msg = String.format("Now playing **%s**.", getTrackTitle(event));
        LAST_CHANNEL.get(guild).sendMessage(msg);
    }

    @EventSubscriber
    public static void onTrackFinish(TrackFinishEvent event)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        IGuild guild = event.getPlayer().getGuild();
        IChannel channel = LAST_CHANNEL.get(guild);
        String msg = String.format("Finished playing **%s**.", getTrackTitle(event));
        channel.sendMessage(msg);

        if (!event.getNewTrack().isPresent()) {
            channel.sendMessage("The playlist is now empty.");
        }
    }

    // Audio player methods
    //---------------------------------------------------------------------------------------------

    static void join(IGuild guild, IChannel channel, IUser user)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        LAST_CHANNEL.put(guild, channel);
        if (user.getConnectedVoiceChannels().size() < 1) {
            channel.sendMessage("You aren't in a voice channel!");
        } else {
            IUser our = channel.getClient().getOurUser();
            IVoiceChannel voice = user.getConnectedVoiceChannels().get(0);
            int userLimit = voice.getUserLimit();

            if (!voice.getModifiedPermissions(our).contains(Permissions.VOICE_CONNECT)) {
                channel.sendMessage("I can't join that voice channel!");
            } else if (userLimit > 0 && voice.getConnectedUsers().size() >= userLimit) {
                channel.sendMessage("That room is full!");
            } else {
                voice.join();
                LAST_VOICE.put(guild, voice);
                String msg = String.format("Connected to **%s**.", voice.getName());
                channel.sendMessage(msg);
            }
        }
    }

    static void leave(IGuild guild, IChannel channel)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        if (!LAST_VOICE.containsKey(guild)) {
            List<IVoiceChannel> cs = channel.getClient().getConnectedVoiceChannels();
            for (IVoiceChannel c : cs) {
                Log.getLogger().trace(c.getName());

                if (c.getGuild() == guild) {
                    LAST_VOICE.put(guild, c);
                    leave(guild, channel);
                    channel.sendMessage("This operation may be delayed or not useful.");
                    return;
                }
            }

            channel.sendMessage("I didn't join any channels!");
            return;
        }

        IVoiceChannel voice = LAST_VOICE.get(guild);
        if (voice.isConnected()) {
            player(channel).clean();
            voice.leave();
            LAST_CHANNEL.remove(guild);
            LAST_VOICE.remove(guild);
            String msg = String.format("Has left the **%s**.", voice.getName());
            channel.sendMessage(msg);
        }
    }

    static void queueUrl(IChannel channel, String spec)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        try {
            URL url = new URL(spec);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("User-Agent", CHROME);
            queue(channel, conn.getInputStream(), url.getFile(), "url", url);
        } catch (MalformedURLException e) {
            channel.sendMessage("That URL is invalid!");
        } catch (IOException e) {
            channel.sendMessage("Connection failed: " + e.getMessage());
        }
    }

    static void queueFile(IChannel channel, String path)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        FileHandle audio = FF.SONGS.child(path);
        if (!audio.exists()) {
            channel.sendMessage("That file doesn't exist!");
        } else if (!audio.canRead()) {
            channel.sendMessage("I don't have access to that file!");
        } else {
            queue(channel, audio.read(0), path, "file", audio);
        }
    }

    static AudioPlayer player(IChannel channel) {
        return AudioPlayer.getAudioPlayerForGuild(channel.getGuild());
    }

    static void list(IChannel channel)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        AudioPlayer player = player(channel);
        List<AudioPlayer.Track> list = player.getPlaylist();
        if (list.size() == 0) {
            channel.sendMessage("No currently playing content.");
            return;
        }

        String status = player.isPaused() ? "Paused" : "Playing";
        for (int i = 0; i < list.size(); i++) {
            AudioPlayer.Track track = list.get(i);
            String title = (String) track.getMetadata().get("title");
            channel.sendMessage(String.format("%s.%s [%s] %s",
                    i + 1,
                    title,
                    track.getTotalTrackTime(),
                    i == 0 ? status : "Wait"));
        }
    }

    // Utility methods
    //---------------------------------------------------------------------------------------------

    private static String getTrackTitle(TrackQueueEvent event) {
        return getTrackTitle(event.getTrack());
    }

    private static String getTrackTitle(TrackStartEvent event) {
        return getTrackTitle(event.getTrack());
    }

    private static String getTrackTitle(TrackFinishEvent event) {
        return getTrackTitle(event.getOldTrack());
    }

    private static String getTrackTitle(AudioPlayer.Track track) {
        Map<String, Object> metadata = track.getMetadata();
        return metadata.containsKey("title") ? metadata.get("title")
                + "" : "Unknown Track";
    }

    private static void queue(IChannel channel, InputStream stream, String title,
                              String key, Object value)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(stream);
            AudioPlayer.Track track = player(channel).queue(audio);
            Map<String, Object> metadata = track.getMetadata();
            metadata.put("title", title);
            if (key != null) {
                metadata.put(key, value);
            }
        } catch (IOException e) {
            channel.sendMessage("An IO exception occured: " + e.getMessage());
            Log.getLogger().debug("", e);
        } catch (UnsupportedAudioFileException e) {
            channel.sendMessage("That type of file is not supported!");
        }
    }

    static void queue(IChannel channel, InputStream stream, String title)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        queue(channel, stream, title, null, null);
    }
}
