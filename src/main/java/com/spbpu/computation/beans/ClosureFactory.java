package com.spbpu.computation.beans;

import com.spbpu.computation.closure.ContinuationFibonacciClosure;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.lang.IgnitePredicate;
import org.springframework.stereotype.Component;

@Component
public class ClosureFactory {
    public ContinuationFibonacciClosure fibonacci(IgnitePredicate<ClusterNode> filter) {
        return new ContinuationFibonacciClosure(filter);
    }
}
