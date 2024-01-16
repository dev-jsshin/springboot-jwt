package com.backend.api.auth.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Authority {

    US_AUTH_TYPE("US", "User"),
    AD_AUTH_TYPE("AD", "Admin");

    private final String value;
    private final String description;
}
