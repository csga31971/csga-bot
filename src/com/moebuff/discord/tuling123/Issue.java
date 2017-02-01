package com.moebuff.discord.tuling123;

/**
 * 基于问答形式的议题
 *
 * @author muto
 */
public interface Issue {

    void ask(String info);

    String getAnswer();
}
