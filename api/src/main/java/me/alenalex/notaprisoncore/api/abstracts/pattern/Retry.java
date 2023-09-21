package me.alenalex.notaprisoncore.api.abstracts.pattern;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Retry<T> {

    private final long timeToWait;
    private final int maxAttempts;
    private final AtomicInteger attempts = new AtomicInteger(0);
    private boolean terminateOnException;

    public Retry(long timeToWait, int maxAttempts) {
        this.timeToWait = timeToWait;
        this.maxAttempts = maxAttempts;
        this.terminateOnException = false;
    }

    public Retry(long timeToWait) {
        this(timeToWait, 5, false);
    }

    public Retry(long timeToWait, int maxAttempts, boolean terminateOnException) {
        this.timeToWait = timeToWait;
        this.maxAttempts = maxAttempts;
        this.terminateOnException = terminateOnException;
    }

    protected abstract Optional<T> work();

    public T doSync(){
        while (shouldRetry()){
            attempts.incrementAndGet();
            try {
                Optional<T> work = work();
                if(work.isPresent())
                    return work.get();

                waitForNextTry();
            }catch (Exception ignored){
                if(terminateOnException)
                    return null;
            }
        }

        return null;
    }

    protected boolean shouldRetry(){
        return attempts.get() < maxAttempts;
    }

    private void waitForNextTry(){
        try {
            Thread.sleep(timeToWait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setTerminateOnException(boolean terminateOnException) {
        this.terminateOnException = terminateOnException;
    }
}
