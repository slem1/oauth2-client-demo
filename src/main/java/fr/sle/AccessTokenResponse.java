package fr.sle;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessTokenResponse {

    private final String accessToken;

    private final String scope;

    private final String tokenType;

    public String accessToken() {
        return accessToken;
    }

    public String scope() {
        return scope;
    }

    public String tokenType() {
        return tokenType;
    }

    public AccessTokenResponse(@JsonProperty("access_token")String accessToken, @JsonProperty("scope") String scope, @JsonProperty("token_type") String tokenType) {
        this.accessToken = accessToken;
        this.scope = scope;
        this.tokenType = tokenType;
    }
}