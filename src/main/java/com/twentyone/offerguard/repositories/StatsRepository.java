package com.twentyone.offerguard.repositories;

import com.twentyone.offerguard.models.Stats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatsRepository extends JpaRepository<Stats, String> {

	Optional<Stats> findByName(String name);

}
