package com.spbpu.computation.configuration;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.DiscoverySpi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("ignite.configuration.main")
public class FibonacciIgniteConfiguration extends IgniteConfiguration {
    @Override
    @Autowired
    @Qualifier("discoverySpi")
    public IgniteConfiguration setDiscoverySpi(DiscoverySpi discoSpi) {
        return super.setDiscoverySpi(discoSpi);
    }
}
