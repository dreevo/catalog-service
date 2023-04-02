package com.polarbookshop.catalogservice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class KeycloakToken {

    private String accessToken;

    @JsonCreator
    private KeycloakToken(@JsonProperty("access_token") final String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
