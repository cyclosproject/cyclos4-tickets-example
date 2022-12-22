package org.socialtrade.cyclostickets.services.impl;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.socialtrade.cyclostickets.apimodels.ClientRegistrationRequest;
import org.socialtrade.cyclostickets.apimodels.ClientRegistrationResponse;
import org.socialtrade.cyclostickets.apimodels.TokenResponse;
import org.socialtrade.cyclostickets.models.CyclosConnection;
import org.socialtrade.cyclostickets.services.ConnectionService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.nimbusds.jwt.SignedJWT;

/**
 * Implementation for {@link ConnectionService}
 */
@Service
public class ConnectionServiceImpl extends BaseService implements ConnectionService {
    private File connectionFile;
    private CyclosConnection _connection;

    @PostConstruct
    public void initialize() {
        var dir = cyclosConfig.getDataDir();
        connectionFile = new File(dir, "cyclos.json");
    }

    @Override
    public CyclosConnection getConnection() {
        if (_connection == null) {
            if (connectionFile.exists()) {
                try {
                    _connection = objectMapper.readValue(connectionFile, CyclosConnection.class);
                } catch (IOException e) {
                    _connection = new CyclosConnection();
                }
            } else {
                _connection = new CyclosConnection();
            }
        }
        return _connection;
    }

    @Override
    public void startOver() {
        connectionFile.delete();
        _connection = null;
    }

    @Override
    public String register() {
        // We need to register a new client in Cyclos
        registerClientIfNeeded();

        // Return the URL to authorize the client
        return authorizeUrl();
    }

    @Override
    public void redirect(String code, String idToken) {
        exchangeToken(code);
        storeUser(idToken);
    }

    @Override
    public String getAccessToken() {
        var connection = getConnection();
        if (connection.getAccessToken() != null
                && connection.getAccessTokenExpiration().isAfter(OffsetDateTime.now().minusSeconds(10))) {
            // The current access token is still valid
            return connection.getAccessToken();
        }

        // Request a new token
        exchangeToken(null);

        // Assume we have a valid access token now
        return getConnection().getAccessToken();
    }

    private void registerClientIfNeeded() {
        var connection = getConnection();
        if (connection.getClientId() != null) {
            // Already registered
            return;
        }

        // Prepare the registration parameters
        var registration = new ClientRegistrationRequest();
        registration.setRedirectUris(Collections.singletonList(getRedirectUri()));
        registration.setClientName("Example Cyclos integration");
        registration.setApplicationType("web");
        registration.setClientUri(cyclosConfig.getAppUri().toASCIIString());
        registration.setResponseTypes(Arrays.asList("code", "id_token"));
        registration.setGrantTypes(Arrays.asList("authorization_code", "implicit"));
        registration.setLogoUri(cyclosConfig.appUri("/logo.png").toASCIIString());

        // Register the client
        HttpResponse<ClientRegistrationResponse> response;
        try {
            response = http.send(HttpRequest.newBuilder()
                    .uri(cyclosConfig.serverUri("/api/oidc/register"))
                    .POST(jsonBodyHelper.publisher(registration))
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .build(),
                    jsonBodyHelper.handler(ClientRegistrationResponse.class));
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        // Store the client id and secret
        var result = response.body();
        connection.setClientId(result.getClientId());
        connection.setClientSecret(result.getClientSecret());
        setConnection(connection);
    }

    private String authorizeUrl() {
        var connection = getConnection();

        return UriComponentsBuilder.newInstance()
                .encode()
                .uri(cyclosConfig.serverUri("/api/oidc/authorize"))
                .queryParam("client_id", connection.getClientId())
                .queryParam("scope", "openid profile offline_access tickets")
                .queryParam("redirect_uri", getRedirectUri())
                .queryParam("response_type", "code id_token")
                .queryParam("response_mode", "query")
                .build()
                .toUri()
                .toASCIIString();
    }

    private String getRedirectUri() {
        return cyclosConfig.appUri("/redirect").toASCIIString();
    }

    private void setConnection(CyclosConnection connection) {
        _connection = connection;
        try {
            objectMapper.writeValue(connectionFile, connection);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void exchangeToken(String code) {
        var connection = getConnection();

        var params = new HashMap<String, String>();
        params.put("client_id", connection.getClientId());
        params.put("client_secret", connection.getClientSecret());
        params.put("grant_type", code == null ? "refresh_token" : "authorization_code");
        if (code != null) {
            params.put("code", code);
            params.put("redirect_uri", getRedirectUri());
        } else {
            params.put("refresh_token", connection.getRefreshToken());
        }
        var body = params.entrySet().stream().map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "="
                + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        // Exchange the authorization code by an access token and refresh token
        HttpResponse<TokenResponse> response;
        try {
            response = http.send(HttpRequest.newBuilder()
                    .uri(cyclosConfig.serverUri("/api/oidc/token"))
                    .POST(BodyPublishers.ofString(body))
                    .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .build(),
                    jsonBodyHelper.handler(TokenResponse.class));
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        // Store the result
        var result = response.body();
        if (code != null) {
            connection.setRefreshToken(result.getRefreshToken());
        }
        connection.setConnected(true);
        connection.setConnectedSince(OffsetDateTime.now());
        connection.setAccessToken(result.getAccessToken());
        Integer exp = result.getExpiresIn();
        connection.setAccessTokenExpiration(OffsetDateTime.now().plusSeconds(exp == null ? 60 : exp));
        setConnection(connection);

        // Also stored the user info
        storeUser(result.getIdToken());
    }

    private void storeUser(String idToken) {
        if (idToken != null) {
            String user;
            try {
                var jwt = SignedJWT.parse(idToken);
                user = jwt.getJWTClaimsSet().getStringClaim("name");
            } catch (Exception e) {
                // Invalid encoding
                return;
            }
            // Update the connection with the claim
            var connection = getConnection();
            connection.setUser(user);
            setConnection(connection);
        }
    }
}
