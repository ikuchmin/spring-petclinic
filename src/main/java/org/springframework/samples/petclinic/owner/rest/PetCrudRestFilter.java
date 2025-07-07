package org.springframework.samples.petclinic.owner.rest;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

public record PetCrudRestFilter(String nameContains, LocalDate birthDate) {
	public Specification<Pet> toSpecification() {
		return Specification.where(nameContainsSpec())
			.and(birthDateSpec());
	}

	private Specification<Pet> nameContainsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(nameContains)
			? cb.like(cb.lower(root.get("name")), "%" + nameContains.toLowerCase() + "%")
			: null);
	}

	private Specification<Pet> birthDateSpec() {
		return ((root, query, cb) -> birthDate != null
			? cb.equal(root.get("birthDate"), birthDate)
			: null);
	}
}
