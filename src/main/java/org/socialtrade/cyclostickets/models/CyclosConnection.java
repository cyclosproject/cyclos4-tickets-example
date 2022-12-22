package org.socialtrade.cyclostickets.models;

import java.time.OffsetDateTime;

/**
 * Represents the Cyclos connection data
 */
public class CyclosConnection {
    private boolean connected;
    private String clientId;
    private String clientSecret;
    private OffsetDateTime connectedSince;
    private String user;
    private String refreshToken;
    private String accessToken;
    private OffsetDateTime accessTokenExpiration;

    public boolean isConnected() {
        return connected;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public OffsetDateTime getConnectedSince() {
        return connectedSince;
    }

    public String getUser() {
        return user;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public OffsetDateTime getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setConnectedSince(OffsetDateTime connectedSince) {
        this.connectedSince = connectedSince;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAccessTokenExpiration(OffsetDateTime accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }
}
