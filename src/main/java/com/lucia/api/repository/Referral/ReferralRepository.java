package com.lucia.api.repository.Referral;

import com.lucia.api.model.entity.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReferralRepository extends JpaRepository<Referral, Long> {
    Optional<Referral> findByReferralCode(String referralCode);
}
