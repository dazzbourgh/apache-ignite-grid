package com.spbpu.computation.configuration;

import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration("discoverySpi")
public class TcpDiscoverySpiConfig extends TcpDiscoverySpi {
    @Override
    @Autowired
    @Qualifier("tcpDiscoveryMulticastIpFinder")
    public TcpDiscoverySpi setIpFinder(TcpDiscoveryIpFinder ipFinder) {
        return super.setIpFinder(ipFinder);
    }

    @Configuration("tcpDiscoveryMulticastIpFinder")
    @ConfigurationProperties("ignite.configuration.finder")
    public static class TcpDiscoveryMulticastIpFinderConfig extends TcpDiscoveryMulticastIpFinder {

    }
}
