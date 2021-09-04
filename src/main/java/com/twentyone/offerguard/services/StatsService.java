package com.twentyone.offerguard.services;

import com.twentyone.offerguard.models.Stats;
import com.twentyone.offerguard.repositories.StatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class StatsService {

	@Autowired
	private StatsRepository statsRepository;

	public void updateStats(Stats stat) {
		log.info("going to update stats in stats table");
		try {
			Optional<Stats> foundStat = statsRepository.findByName(stat.getName());
			if (foundStat.isPresent()) {
				foundStat.get().setValue(stat.getValue());
				statsRepository.save(foundStat.get());
			} else {
				statsRepository.save(stat);
			}
			log.info("update stats successful");
		} catch (Exception e) {
			log.error("error in updating stats in stats table", e);
		}
	}
}
