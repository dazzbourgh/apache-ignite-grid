package com.spbpu.computation.beans;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IgniteFactory {
    @Autowired
    private IgniteConfiguration igniteConfiguration;

    public Ignite ignite() {
        return Ignition.start(igniteConfiguration);
    }
}
