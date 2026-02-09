package org.springframework.samples.petclinic.featureflag.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.featureflag.annotation.FeatureToggle;
//import org.springframework.samples.petclinic.featureflag.exception.FeatureDisabledException;
import org.springframework.samples.petclinic.featureflag.service.FeatureFlagService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@RequiredArgsConstructor
public class FeatureToggleAspect {

	private final FeatureFlagService featureFlagService;
	private final HttpServletRequest request;

	@Before("@annotation(featureToggle)")
	public void checkFeature(JoinPoint joinPoint, FeatureToggle featureToggle) {

		String featureKey = featureToggle.value();

		// User identifier (simple & flexible)
		String userId = resolveUserId();

		boolean enabled =
			featureFlagService.isEnabled(featureKey, userId);

		if (!enabled) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Feature " + featureKey + " is disabled.");
		}
	}

	/**
	 * Very flexible user resolution strategy
	 * Can be replaced later with auth / session logic
	 */
	private String resolveUserId() {
		String headerUser = request.getHeader("X-USER-ID");
		return headerUser != null ? headerUser : "anonymous";
	}
}
