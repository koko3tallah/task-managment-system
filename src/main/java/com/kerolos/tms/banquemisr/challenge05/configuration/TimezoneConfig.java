package com.kerolos.tms.banquemisr.challenge05.configuration;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class TimezoneConfig {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
