package com.savbill.integrationsystem.nms.entity;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

public class TokenManager {

    private String jwtToken;
    private LocalDateTime expiryTime;
    private final ReentrantLock lock = new ReentrantLock();

    private static final TokenManager INSTANCE = new TokenManager();

    private TokenManager() {
    }

    public static TokenManager getInstance() {
        return INSTANCE;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void updateToken(String jwtToken, LocalDateTime expiryTime) {
        lock.lock();
        try {
            this.jwtToken = jwtToken;
            this.expiryTime = expiryTime;
        } finally {
            lock.unlock();
        }
    }

    public boolean isTokenExpired() {
        return expiryTime == null || LocalDateTime.now().isAfter(expiryTime);
    }
}