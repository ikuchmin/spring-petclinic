package org.springframework.samples.petclinic.vet.rest;

import jakarta.validation.constraints.NotBlank;
import org.springframework.samples.petclinic.vet.Specialty;

import java.util.Objects;

/**
 * DTO for {@link Specialty}
 */
public class SpecialtyCrudRestDto {
	private Integer id;
	@NotBlank
	private String name;

	public SpecialtyCrudRestDto() {
	}

	public SpecialtyCrudRestDto(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SpecialtyCrudRestDto entity = (SpecialtyCrudRestDto) o;
		return Objects.equals(this.id, entity.id) &&
			Objects.equals(this.name, entity.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +
			"id = " + id + ", " +
			"name = " + name + ")";
	}
}
