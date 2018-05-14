package com.spbpu.computation;

import com.spbpu.computation.beans.IgniteFactory;
import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

@SpringBootApplication
@ImportResource("classpath:config.xml")
public class ComputationApplication implements CommandLineRunner {
    @Autowired
    private IgniteFactory igniteFactory;

    public static void main(String[] args) {
        SpringApplication.run(ComputationApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try (Ignite ignite = igniteFactory.ignite()) {
            ClusterGroup clusterGroup = ignite.cluster().forRemotes();
            Collection<Integer> result = ignite.compute(clusterGroup)
                    .apply((String line) -> Arrays.stream(line.split(" "))
                                    .mapToInt(String::length)
                                    .sum(),
                            Arrays.asList("Some text\nThat takes\nSeveral lines".split("\n")));
            System.out.println("The result is: " + result.stream()
                    .mapToInt(it -> it)
                    .sum());
        }
    }
}
