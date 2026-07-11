package com.konceptbuild.core;

import com.konceptbuild.core.entity.RevokedTokenEntity;
import com.konceptbuild.core.repository.RevokedTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class TokenRevocationServiceImpl implements TokenRevocationService {
    private final RevokedTokenRepository revokedTokenRepository;

    public TokenRevocationServiceImpl(RevokedTokenRepository revokedTokenRepository) {
        this.revokedTokenRepository = revokedTokenRepository;
    }

    @Override
    public boolean isRevoked(UUID tokenId) {
        return revokedTokenRepository.existsById(tokenId);
    }

    @Override
    @Transactional
    public void revoke(UUID tokenId, Instant expiresAt) {
        if (!revokedTokenRepository.existsById(tokenId)) {
            revokedTokenRepository.save(new RevokedTokenEntity(tokenId, expiresAt));
        }
    }
}
