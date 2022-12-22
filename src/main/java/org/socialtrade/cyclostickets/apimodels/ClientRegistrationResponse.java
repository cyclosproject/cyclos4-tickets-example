package org.socialtrade.cyclostickets.apimodels;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response from the client registration request
 */
public class ClientRegistrationResponse {
    private String clientId;
    private String clientSecret;

    @JsonProperty("client_id")
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @JsonProperty("client_secret")
    public String getClientSecret() {
        return clientSecret;
    }
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

}
