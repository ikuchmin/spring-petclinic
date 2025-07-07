package org.springframework.samples.petclinic.owner.rest;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.owner.Visit;

import java.time.LocalDate;

public record VisitCrudRestFilter(LocalDate date) {
	public Specification<Visit> toSpecification() {
		return Specification.where(dateSpec());
	}

	private Specification<Visit> dateSpec() {
		return ((root, query, cb) -> date != null
			? cb.equal(root.get("date"), date)
			: null);
	}
}
