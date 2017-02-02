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
     * @return 答复
     * @throws TuringException 如果服务端发生异常
     */
    Issue talk(Issue issue) throws TuringException;
}
