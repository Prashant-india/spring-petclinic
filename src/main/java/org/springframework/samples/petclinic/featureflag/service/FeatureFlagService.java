package org.springframework.samples.petclinic.featureflag.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.featureflag.model.FeatureFlag;
import org.springframework.samples.petclinic.featureflag.repository.FeatureFlagRepository;
import org.springframework.stereotype.Service;

//@Service
//public class FeatureFlagService {
//
//	@Autowired
//	FeatureFlagRepository repo;
//	public boolean isEnabled(String featureKey, String userId) {
//		FeatureFlag flag = repo.findByFeatureKey(featureKey)
//			.orElseThrow(...);
//
//		if (!flag.isEnabled()) return false;
//		if (flag.getBlacklistUsers().contains(userId)) return false;
//		if (flag.getWhitelistUsers().contains(userId)) return true;
//
//		return rolloutCheck(flag.getRolloutPercentage(), userId);
//	}
//}

import lombok.RequiredArgsConstructor;
import org.springframework.samples.petclinic.featureflag.model.FeatureFlag;
import org.springframework.samples.petclinic.featureflag.repository.FeatureFlagRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FeatureFlagService {

	private final FeatureFlagRepository featureFlagRepository;

	/**
	 * Core helper method – can be called from controllers, services, aspects
	 */
	public boolean isEnabled(String featureKey, String userId) {

		Optional<FeatureFlag> optionalFlag =
			featureFlagRepository.findByFeatureKey(featureKey);

		// ---- Edge case 1: flag not found → fail safe OFF
		if (optionalFlag.isEmpty()) {
			return false;
		}

		FeatureFlag flag = optionalFlag.get();

		// ---- Edge case 2: globally disabled
		if (!flag.isEnabled()) {
			return false;
		}

		// ---- Normalize userId (avoid null issues)
		String safeUserId = (userId == null) ? "anonymous" : userId;

		Set<String> blacklist = flag.getBlacklistUsers();
		Set<String> whitelist = flag.getWhitelistUsers();

		// ---- Edge case 3: blacklisted user always blocked
		if (blacklist != null && blacklist.contains(safeUserId)) {
			return false;
		}

		// ---- Edge case 4: whitelisted user always allowed
		if (whitelist != null && whitelist.contains(safeUserId)) {
			return true;
		}

		// ---- Edge case 5: percentage rollout
		Integer rollout = flag.getRolloutPercentage();

		if (rollout == null || rollout <= 0) {
			return false;
		}

		if (rollout >= 100) {
			return true;
		}

		return isUserInRolloutPercentage(safeUserId, rollout);
	}

	/**
	 * Deterministic percentage rollout
	 * Same user will always get same result
	 */
	private boolean isUserInRolloutPercentage(String userId, int rolloutPercentage) {

		int hash = Math.abs(
			userId.getBytes(StandardCharsets.UTF_8)[0]
		);

		int bucket = hash % 100;

		return bucket < rolloutPercentage;
	}
}


