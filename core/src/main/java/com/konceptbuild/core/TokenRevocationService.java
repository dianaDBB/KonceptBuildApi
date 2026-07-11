package com.konceptbuild.core;

import java.time.Instant;
import java.util.UUID;

public interface TokenRevocationService {
    boolean isRevoked(UUID tokenId);

    void revoke(UUID tokenId, Instant expiresAt);
}
