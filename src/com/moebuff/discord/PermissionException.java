package com.moebuff.discord;

/**
 * 由权限控制产生，当用户越权时，抛出该异常，以终止当前操作。
 *
 * @author muto
 */
public class PermissionException extends Exception {
    public PermissionException() {
    }

    public PermissionException(String message) {
        super(message);
    }

    public PermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PermissionException(Throwable cause) {
        super(cause);
    }

    public PermissionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
