package com.moebuff.discord.beatmap;

import lombok.Data;

/**
 * 乐谱信息
 *
 * @author muto
 */
@Data
public class BeatmapInfo {
    private String title;
    private String titleUnicode;
    private String artist;
    private String artistUnicode;
    private String creator;

    private String path;
    private String source;
    private String tags;
    private String music;

    private int offset;
    private boolean favorite;
}
