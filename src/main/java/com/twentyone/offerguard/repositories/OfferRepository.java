package com.twentyone.offerguard.repositories;

import com.twentyone.offerguard.models.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<Offer, String> {
}
