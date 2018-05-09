package com.spbpu.computation;

import com.spbpu.computation.beans.ClosureFactory;
import com.spbpu.computation.beans.IgniteFactory;
import com.spbpu.computation.closure.ContinuationFibonacciClosure;
import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.lang.IgnitePredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigInteger;
import java.util.UUID;

@SpringBootApplication
public class ComputationApplication implements CommandLineRunner {
    @Autowired
    private IgniteFactory igniteFactory;
    @Autowired
    private ClosureFactory closureFactory;

    public static void main(String[] args) {
        SpringApplication.run(ComputationApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try (Ignite ignite = igniteFactory.ignite()) {
            long N = 8;
            final UUID exampleNodeId = ignite.cluster()
                    .localNode()
                    .id();
            IgnitePredicate<ClusterNode> nodeFilter = n -> ignite.cluster()
                    .forRemotes()
                    .nodes()
                    .isEmpty() || !n.id().equals(exampleNodeId);
            ContinuationFibonacciClosure closure = closureFactory
                    .fibonacci(nodeFilter);
            BigInteger result = ignite.compute(ignite.cluster().forPredicate(nodeFilter))
                    .apply(closure, N);
            System.out.println("The result is: " + result);
        }
    }
}
