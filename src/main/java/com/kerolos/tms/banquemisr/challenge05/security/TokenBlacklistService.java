package com.kerolos.tms.banquemisr.challenge05.security;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class TokenBlacklistService {

    private final Set<String> tokenBlacklist = new CopyOnWriteArraySet<>();

    public void addToBlacklist(String token) {
        tokenBlacklist.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }

}
