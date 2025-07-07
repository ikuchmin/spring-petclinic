package org.springframework.samples.petclinic.owner.rest;

import jakarta.validation.constraints.NotBlank;
import org.springframework.samples.petclinic.owner.Pet;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

/**
 * DTO for {@link Pet}
 */
public class PetCrudRestDto {
	private Integer id;
	@NotBlank
	private String name;
	private LocalDate birthDate;
	private Integer typeId;
	private Set<Integer> visitIds;

	public PetCrudRestDto() {
	}

	public PetCrudRestDto(Integer id, String name, LocalDate birthDate, Integer typeId, Set<Integer> visitIds) {
		this.id = id;
		this.name = name;
		this.birthDate = birthDate;
		this.typeId = typeId;
		this.visitIds = visitIds;
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

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Set<Integer> getVisitIds() {
		return visitIds;
	}

	public void setVisitIds(Set<Integer> visitIds) {
		this.visitIds = visitIds;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PetCrudRestDto entity = (PetCrudRestDto) o;
		return Objects.equals(this.id, entity.id) &&
			Objects.equals(this.name, entity.name) &&
			Objects.equals(this.birthDate, entity.birthDate) &&
			Objects.equals(this.typeId, entity.typeId) &&
			Objects.equals(this.visitIds, entity.visitIds);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, birthDate, typeId, visitIds);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +
			"id = " + id + ", " +
			"name = " + name + ", " +
			"birthDate = " + birthDate + ", " +
			"typeId = " + typeId + ", " +
			"visitIds = " + visitIds + ")";
	}
}
