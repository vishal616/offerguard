package com.twentyone.offerguard.repositories;

import com.twentyone.offerguard.models.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, String> {

	List<Offer> findByAffiliateStatus(String affiliateStatus);
}
