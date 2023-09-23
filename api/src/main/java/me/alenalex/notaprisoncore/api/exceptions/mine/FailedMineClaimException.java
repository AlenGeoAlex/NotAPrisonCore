package me.alenalex.notaprisoncore.api.exceptions.mine;

public final class FailedMineClaimException extends RuntimeException{

    public FailedMineClaimException(String message) {
        super(message);
    }

    public FailedMineClaimException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedMineClaimException(Throwable cause) {
        super(cause);
    }

    public FailedMineClaimException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
