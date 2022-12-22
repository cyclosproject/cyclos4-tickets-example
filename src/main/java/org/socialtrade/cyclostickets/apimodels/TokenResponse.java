package org.socialtrade.cyclostickets.apimodels;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response from the token exchange request
 */
public class TokenResponse {
    private String accessToken;
    private String tokenType;
    private String idToken;
    private String state;
    private Integer expiresIn;
    private String refreshToken;

    @JsonProperty("access_token")
    public String getAccessToken() {
      return accessToken;
    }
    public void setAccessToken(String accessToken) {
      this.accessToken = accessToken;
    }

    @JsonProperty("token_type")
    public String getTokenType() {
      return tokenType;
    }
    public void setTokenType(String tokenType) {
      this.tokenType = tokenType;
    }

    @JsonProperty("id_token")
    public String getIdToken() {
      return idToken;
    }
    public void setIdToken(String idToken) {
      this.idToken = idToken;
    }

    @JsonProperty("state")
    public String getState() {
      return state;
    }
    public void setState(String state) {
      this.state = state;
    }

    @JsonProperty("expires_in")
    public Integer getExpiresIn() {
      return expiresIn;
    }
    public void setExpiresIn(Integer expiresIn) {
      this.expiresIn = expiresIn;
    }

    @JsonProperty("refresh_token")
    public String getRefreshToken() {
      return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
      this.refreshToken = refreshToken;
    }
}
