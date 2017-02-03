package com.moebuff.discord.tuling123;

import lombok.Data;

/**
 * 旨在复读的议题
 *
 * @author muto
 */
@Data
public class BaseIssue implements Issue {
    protected String userId;
    protected String info;

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
