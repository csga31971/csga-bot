package com.moebuff.discord.tuling123;

import com.moebuff.discord.utils.UnhandledException;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

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

    BaseIssue(Issue issue) {
        try {
            BeanUtils.copyProperties(this, issue);
        } catch (ReflectiveOperationException e) {
            throw new UnhandledException(e);
        }
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
