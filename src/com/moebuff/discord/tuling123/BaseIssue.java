package com.moebuff.discord.tuling123;

/**
 * 旨在复读的议题
 *
 * @author muto
 */
public class BaseIssue implements Issue {
    private String userId;
    private String info;

    BaseIssue(String userId) {
        this.userId = userId;
    }

    @Override
    public void ask(String info) {
        this.info = info;
    }

    @Override
    public String getAnswer() {
        return info;
    }
}
