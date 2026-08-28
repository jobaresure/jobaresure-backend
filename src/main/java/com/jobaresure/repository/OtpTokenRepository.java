package com.jobaresure.repository;

import com.jobaresure.entity.OtpToken;
import com.jobaresure.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, UUID> {

    /** Most recent non-consumed OTP for an identifier + purpose. */
    Optional<OtpToken> findFirstByIdentifierAndPurposeAndConsumedFalseOrderByCreatedAtDesc(
            String identifier, OtpPurpose purpose);

    @Modifying
    @Query("UPDATE OtpToken o SET o.consumed = true " +
            "WHERE o.identifier = :identifier AND o.purpose = :purpose AND o.consumed = false")
    void invalidateActiveTokens(@Param("identifier") String identifier,
                                @Param("purpose") OtpPurpose purpose);

    @Modifying
    @Query("DELETE FROM OtpToken o WHERE o.expiresAt < :cutoff")
    int deleteExpiredBefore(@Param("cutoff") LocalDateTime cutoff);
}
