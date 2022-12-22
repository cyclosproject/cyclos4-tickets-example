package org.socialtrade.cyclostickets.services;

import org.socialtrade.cyclostickets.models.CyclosConnection;

/**
 * Service that controls the Cyclos OpenID Connect / OAuth connection
 */
public interface ConnectionService {

    /**
     * Returns information about the current connection
     */
    CyclosConnection getConnection();

    /**
     * Starts an OIDC client registration and returns the URL to which the user should be redirected to authorize the request
     */
    String register();

    /**
     * Removes the current connection information and starts all over
     */
    void startOver();

    /**
     * Handles the redirect after the authorization request
     */
    void redirect(String code, String idToken);

    /**
     * Returns a valid access token. When the current one has expired, requests a new one.
     */
    String getAccessToken();

}
