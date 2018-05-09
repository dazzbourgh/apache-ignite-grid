package com.spbpu.computation.closure;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.compute.ComputeJobContext;
import org.apache.ignite.lang.IgniteClosure;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.lang.IgniteInClosure;
import org.apache.ignite.lang.IgnitePredicate;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.JobContextResource;
import org.springframework.lang.Nullable;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

@NoArgsConstructor
@RequiredArgsConstructor
public class ContinuationFibonacciClosure implements IgniteClosure<Long, BigInteger> {
    /** Future for spawned task. */
    private IgniteFuture<BigInteger> fut1;

    /** Future for spawned task. */
    private IgniteFuture<BigInteger> fut2;

    /** Auto-inject job context. */
    @JobContextResource
    private ComputeJobContext jobCtx;

    /** Auto-inject ignite instance. */
    @IgniteInstanceResource
    private Ignite ignite;

    /** Predicate. */
    @NonNull
    private IgnitePredicate<ClusterNode> nodeFilter;

    /** {@inheritDoc} */
    @Nullable
    @Override public BigInteger apply(Long n) {
        if (fut1 == null || fut2 == null) {
            System.out.println();
            System.out.println(">>> Starting fibonacci execution for number: " + n);

            // Make sure n is not negative.
            n = Math.abs(n);

            if (n <= 2)
                return n == 0 ? BigInteger.ZERO : BigInteger.ONE;

            // Node-local storage.
            ConcurrentMap<Long, IgniteFuture<BigInteger>> locMap = ignite.cluster().nodeLocalMap();

            // Check if value is cached in node-local-map first.
            fut1 = locMap.get(n - 1);
            fut2 = locMap.get(n - 2);

            ClusterGroup p = ignite.cluster().forPredicate(nodeFilter);

            IgniteCompute compute = ignite.compute(p);

            // If future is not cached in node-local-map, cache it.
            if (fut1 == null) {
                IgniteFuture<BigInteger> futVal = compute.applyAsync(
                        new ContinuationFibonacciClosure(nodeFilter), n - 1);

                fut1 = locMap.putIfAbsent(n - 1, futVal);

                if (fut1 == null)
                    fut1 = futVal;
            }

            // If future is not cached in node-local-map, cache it.
            if (fut2 == null) {
                IgniteFuture<BigInteger> futVal = compute.applyAsync(
                        new ContinuationFibonacciClosure(nodeFilter), n - 2);

                fut2 = locMap.putIfAbsent(n - 2, futVal);

                if (fut2 == null)
                    fut2 = futVal;
            }

            // If futures are not done, then wait asynchronously for the result
            if (!fut1.isDone() || !fut2.isDone()) {
                IgniteInClosure<IgniteFuture<BigInteger>> lsnr = (IgniteInClosure<IgniteFuture<BigInteger>>) f -> {
                    // If both futures are done, resume the continuation.
                    if (fut1.isDone() && fut2.isDone())
                        // CONTINUATION:
                        // =============
                        // Resume suspended job execution.
                        jobCtx.callcc();
                };

                // CONTINUATION:
                // =============
                // Hold (suspend) job execution.
                // It will be resumed in listener above via 'callcc()' call
                // once both futures are done.
                jobCtx.holdcc();

                // Attach the same listener to both futures.
                fut1.listen(lsnr);
                fut2.listen(lsnr);

                return null;
            }
        }

        assert fut1.isDone() && fut2.isDone();

        // Return cached results.
        return fut1.get().add(fut2.get());
    }
}