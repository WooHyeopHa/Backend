package com.whh.findmuseapi.jwt.property;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenProperty {
    private Long expiration;
    private String header;
}
