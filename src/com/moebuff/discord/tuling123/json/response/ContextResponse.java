package com.moebuff.discord.tuling123.json.response;

import java.util.List;

/**
 * 对话结果
 *
 * @author muto
 */
public class ContextResponse<T> {
    private String code;
    private String text;
    private String url;
    private List<T> list;
}
