package com.spbpu.computation;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.lang.IgniteClosure;

import java.util.Arrays;
import java.util.Collection;

public class ComputationApplication {
    public static void main(String[] args) {
        try (Ignite ignite = Ignition.start("config/client.xml")) {
            ignite.configuration().setClientMode(true);
            ClusterGroup clusterGroup = ignite.cluster().forRemotes();
            Collection<Integer> result = ignite.compute(clusterGroup)
                    .apply(new IgniteClosure<String, Integer>() {
                               @Override
                               public Integer apply(String line) {
                                   System.out.println("Computing for line:\n\t" + line);
                                   String[] words = line.split(" ");
                                   int count = 0;
                                   for (String word : words) {
                                       ++count;
                                   }
                                   return count;
                               }
                           },
                            Arrays.asList("Some text\nThat takes\nSeveral lines".split("\n")));
            int resultNumber = 0;
            for (Integer i : result) {
                resultNumber += i;
            }
            System.out.println("The result is: " + resultNumber);
        }
    }
}
