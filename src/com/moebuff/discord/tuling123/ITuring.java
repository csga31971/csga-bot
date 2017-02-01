package com.moebuff.discord.tuling123;

/**
 * 图灵接口
 *
 * @author muto
 */
public interface ITuring {
    /**
     * 发起一次对话
     *
     * @param issue 议题
     */
    void talk(Issue issue) throws TuringException;
}
