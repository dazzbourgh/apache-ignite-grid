package com.spbpu.computation;

import com.spbpu.computation.beans.IgniteFactory;
import org.apache.ignite.Ignite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ComputationApplication implements CommandLineRunner {
    @Autowired
    private IgniteFactory igniteFactory;

    public static void main(String[] args) {
        SpringApplication.run(ComputationApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try (Ignite ignite = igniteFactory.ignite()) {
            System.out.println("works");
        }
    }
}
