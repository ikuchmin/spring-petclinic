package org.springframework.samples.petclinic.owner.rest;

import jakarta.validation.constraints.NotBlank;
import org.springframework.samples.petclinic.owner.Visit;

import java.time.LocalDate;
import java.util.Objects;

/**
 * DTO for {@link Visit}
 */
public class VisitCrudRestDto {
	private Integer id;
	private LocalDate date;
	@NotBlank
	private String description;

	public VisitCrudRestDto() {
	}

	public VisitCrudRestDto(Integer id, LocalDate date, String description) {
		this.id = id;
		this.date = date;
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VisitCrudRestDto entity = (VisitCrudRestDto) o;
		return Objects.equals(this.id, entity.id) &&
			Objects.equals(this.date, entity.date) &&
			Objects.equals(this.description, entity.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, date, description);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +
			"id = " + id + ", " +
			"date = " + date + ", " +
			"description = " + description + ")";
	}
}
