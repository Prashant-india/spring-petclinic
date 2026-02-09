package org.springframework.samples.petclinic.featureflag.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.featureflag.model.FeatureFlag;
import org.springframework.samples.petclinic.featureflag.repository.FeatureFlagRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flags")
@RequiredArgsConstructor
public class FeatureFlagController {

	private final FeatureFlagRepository featureFlagRepository;

	// ---------------- CREATE ----------------
	@PostMapping
	public ResponseEntity<FeatureFlag> createFlag(
		@RequestBody FeatureFlag featureFlag) {

		FeatureFlag saved = featureFlagRepository.save(featureFlag);
		return new ResponseEntity<>(saved, HttpStatus.CREATED);
	}

	// ---------------- READ ALL ----------------
	@GetMapping
	public List<FeatureFlag> getAllFlags() {
		return featureFlagRepository.findAll();
	}

	// ---------------- READ BY KEY ----------------
	@GetMapping("/{featureKey}")
	public ResponseEntity<FeatureFlag> getByKey(
		@PathVariable String featureKey) {

		return featureFlagRepository.findByFeatureKey(featureKey)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	// ---------------- UPDATE ----------------
	@PutMapping("/{featureKey}")
	public ResponseEntity<FeatureFlag> updateFlag(
		@PathVariable String featureKey,
		@RequestBody FeatureFlag updatedFlag) {

		return featureFlagRepository.findByFeatureKey(featureKey)
			.map(existing -> {
				existing.setEnabled(updatedFlag.isEnabled());
				existing.setRolloutPercentage(updatedFlag.getRolloutPercentage());
				existing.setWhitelistUsers(updatedFlag.getWhitelistUsers());
				existing.setBlacklistUsers(updatedFlag.getBlacklistUsers());

				FeatureFlag saved = featureFlagRepository.save(existing);
				return ResponseEntity.ok(saved);
			})
			.orElse(ResponseEntity.notFound().build());
	}

	// ---------------- DELETE ----------------
	@DeleteMapping("/{featureKey}")
	public ResponseEntity<Object> deleteFlag(
		@PathVariable String featureKey) {

		return featureFlagRepository.findByFeatureKey(featureKey)
			.map(flag -> {
				featureFlagRepository.delete(flag);
				return ResponseEntity.noContent().build();
			})
			.orElse(ResponseEntity.notFound().build());
	}
}

