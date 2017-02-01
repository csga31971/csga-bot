package com.moebuff.discord.tuling123;

/**
 * 当图灵接口出现问题时，用于封装服务端发来的异常
 *
 * @author muto
 */
public class TuringException extends Exception {
    public TuringException() {
    }

    public TuringException(String message) {
        super(message);
    }

    public TuringException(String message, Throwable cause) {
        super(message, cause);
    }

    public TuringException(Throwable cause) {
        super(cause);
    }

    public TuringException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
