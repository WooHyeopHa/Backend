package com.whh.findmuseapi.ios.dto.key;

import java.util.List;
import lombok.Getter;

@Getter
public class ApplePublicKeys {
    private List<ApplePublicKey> keys;
    public ApplePublicKey getMatchedKey(String kid, String alg) {
        return keys.stream()
            .filter(key -> key.getKid().equals(kid) && key.getAlg().equals(alg))
            .findAny()
            .orElseThrow();
    }
}
