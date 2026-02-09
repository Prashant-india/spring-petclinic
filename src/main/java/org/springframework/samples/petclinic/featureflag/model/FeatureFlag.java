package org.springframework.samples.petclinic.featureflag.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "feature_flags")
@Getter
@Setter
public class FeatureFlag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "feature_key", unique = true, nullable = false)
	private String featureKey;

	@Column(nullable = false)
	private boolean enabled;

	@Column(name = "rollout_percentage")
	private Integer rolloutPercentage;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
		name = "feature_flag_whitelist",
		joinColumns = @JoinColumn(name = "feature_flag_id")
	)
	@Column(name = "user_id")
	private Set<String> whitelistUsers;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
		name = "feature_flag_blacklist",
		joinColumns = @JoinColumn(name = "feature_flag_id")
	)
	@Column(name = "user_id")
	private Set<String> blacklistUsers;
}
