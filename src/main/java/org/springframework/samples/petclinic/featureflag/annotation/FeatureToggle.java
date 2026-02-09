package org.springframework.samples.petclinic.featureflag.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeatureToggle {

	/**
	 * Feature key stored in DB
	 * Example: ADD_PET, ADD_VISIT
	 */
	String value();
}
