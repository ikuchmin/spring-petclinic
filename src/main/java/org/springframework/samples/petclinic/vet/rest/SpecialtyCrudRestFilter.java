package org.springframework.samples.petclinic.vet.rest;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.util.StringUtils;

public record SpecialtyCrudRestFilter(String nameContains) {
	public Specification<Specialty> toSpecification() {
		return Specification.where(nameContainsSpec());
	}

	private Specification<Specialty> nameContainsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(nameContains)
			? cb.like(cb.lower(root.get("name")), "%" + nameContains.toLowerCase() + "%")
			: null);
	}
}
