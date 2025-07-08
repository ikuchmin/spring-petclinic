package org.springframework.samples.petclinic.vet.rest;

import jakarta.validation.constraints.NotBlank;
import org.springframework.samples.petclinic.vet.Vet;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * DTO for {@link Vet}
 */
public class VetCrudRestDto {
	private Integer id;
	@NotBlank
	private String firstName;
	@NotBlank
	private String lastName;
	private List<Integer> specialtyIds;

	public VetCrudRestDto() {
	}

	public VetCrudRestDto(Integer id, String firstName, String lastName, List<Integer> specialtyIds) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.specialtyIds = specialtyIds;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<Integer> getSpecialtyIds() {
		return specialtyIds;
	}

	public void setSpecialtyIds(List<Integer> specialtyIds) {
		this.specialtyIds = specialtyIds;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VetCrudRestDto entity = (VetCrudRestDto) o;
		return Objects.equals(this.id, entity.id) &&
			Objects.equals(this.firstName, entity.firstName) &&
			Objects.equals(this.lastName, entity.lastName) &&
			Objects.equals(this.specialtyIds, entity.specialtyIds);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, firstName, lastName, specialtyIds);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +
			"id = " + id + ", " +
			"firstName = " + firstName + ", " +
			"lastName = " + lastName + ", " +
			"specialtyIds = " + specialtyIds + ")";
	}
}
