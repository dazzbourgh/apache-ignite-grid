package com.spbpu.computation;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterGroup;

import java.util.Arrays;
import java.util.Collection;

public class ComputationApplication {
    public static void main(String[] args) {
        try (Ignite ignite = Ignition.start("config/client.xml")) {
            ClusterGroup clusterGroup = ignite.cluster().forServers();
            Collection<Integer> result = ignite.compute(clusterGroup)
                    .apply((String line) -> {
                                System.out.println("Computing for line:\n\t" + line);
                                return Arrays.stream(line.split(" "))
                                        .mapToInt(word -> 1)
                                        .sum();
                            },
                            Arrays.asList("Some text\nThat takes\nSeveral lines".split("\n")));
            System.out.println("The result is: " + result.stream()
                    .mapToInt(it -> it)
                    .sum());
        }
    }
}
