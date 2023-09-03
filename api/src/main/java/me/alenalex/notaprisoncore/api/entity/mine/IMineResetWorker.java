package me.alenalex.notaprisoncore.api.entity.mine;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.concurrent.CompletableFuture;

public interface IMineResetWorker {

    boolean isAsync();

    long getStartedOn();

    long duration();

    CompletableFuture<WorkerResponse> reset();

    @Getter
    @EqualsAndHashCode
    @ToString
    public static final class WorkerResponse {
        private final long started;
        private final long ended;
        private final long blocksReplaced;
        private final boolean completedSuccessfully;

        public WorkerResponse(long started, long ended, long blocksReplaced, boolean completedSuccessfully) {
            this.started = started;
            this.ended = ended;
            this.blocksReplaced = blocksReplaced;
            this.completedSuccessfully = completedSuccessfully;
        }
    }
}
