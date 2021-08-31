package com.twentyone.offerguard.repositories;

import com.twentyone.offerguard.models.RedirectUrl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RedirectUrlRepository extends JpaRepository<RedirectUrl, Integer> {
}
