package com.coditory.sherlock.reactor;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

final class DistributedLockExecutor {
    private DistributedLockExecutor() {
        throw new UnsupportedOperationException("Do not instantiate utility class");
    }

    static <T> Mono<T> executeOnAcquired(
        Mono<AcquireResult> lockResult,
        Mono<T> mono,
        Supplier<Mono<ReleaseResult>> release
    ) {
        return lockResult
            .filter(AcquireResult::isAcquired)
            .flatMap(acquiredLockResult ->
                mono
                    .flatMap(result -> release.get().map(__ -> result))
                    .switchIfEmpty(release.get().then(Mono.empty()))
                    .onErrorResume(throwable -> release.get().flatMap(r -> Mono.error(throwable)))
            );
    }
}
