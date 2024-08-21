package com.whh.findmuseapi.ios.dto.key;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ApplePublicKey {
    private String kty;
    private String kid;
    private String use;
    private String alg;
    private String n;
    private String e;
}
