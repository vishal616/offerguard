package com.twentyone.offerguard.repositories;

import com.twentyone.offerguard.models.RedirectUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RedirectUrlRepository extends JpaRepository<RedirectUrl, Integer> {

	List<RedirectUrl> findByOfferId(String offerId);
}
