package org.springframework.samples.petclinic.vet.rest;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.util.StringUtils;

import java.util.List;

public record VetCrudRestFilter(String firstNameContains, String lastNameContains, List<Integer> specialtiesIdIn) {
	public Specification<Vet> toSpecification() {
		return Specification.where(firstNameContainsSpec())
			.and(lastNameContainsSpec())
			.and(specialtiesIdInSpec());
	}

	private Specification<Vet> firstNameContainsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(firstNameContains)
			? cb.like(cb.lower(root.get("firstName")), "%" + firstNameContains.toLowerCase() + "%")
			: null);
	}

	private Specification<Vet> lastNameContainsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(lastNameContains)
			? cb.like(cb.lower(root.get("lastName")), "%" + lastNameContains.toLowerCase() + "%")
			: null);
	}

	private Specification<Vet> specialtiesIdInSpec() {
		return ((root, query, cb) -> specialtiesIdIn != null
			? root.get("specialties").get("id").in(specialtiesIdIn)
			: null);
	}
}
