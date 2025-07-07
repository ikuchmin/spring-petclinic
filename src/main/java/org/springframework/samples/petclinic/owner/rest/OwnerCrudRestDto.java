package org.springframework.samples.petclinic.owner.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.samples.petclinic.owner.Owner;

import java.util.List;
import java.util.Objects;

/**
 * DTO for {@link Owner}
 */
public class OwnerCrudRestDto {
	private Integer id;
	@NotBlank
	private String firstName;
	@NotBlank
	private String lastName;
	@NotBlank
	private String address;
	@NotBlank
	private String city;
	@Pattern(message = "{telephone.invalid}", regexp = "\\d{10}")
	@NotBlank
	private String telephone;
	private List<Integer> petIds;

	public OwnerCrudRestDto() {
	}

	public OwnerCrudRestDto(Integer id, String firstName, String lastName, String address, String city, String telephone, List<Integer> petIds) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.telephone = telephone;
		this.petIds = petIds;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public List<Integer> getPetIds() {
		return petIds;
	}

	public void setPetIds(List<Integer> petIds) {
		this.petIds = petIds;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OwnerCrudRestDto entity = (OwnerCrudRestDto) o;
		return Objects.equals(this.id, entity.id) &&
			Objects.equals(this.firstName, entity.firstName) &&
			Objects.equals(this.lastName, entity.lastName) &&
			Objects.equals(this.address, entity.address) &&
			Objects.equals(this.city, entity.city) &&
			Objects.equals(this.telephone, entity.telephone) &&
			Objects.equals(this.petIds, entity.petIds);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, firstName, lastName, address, city, telephone, petIds);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +
			"id = " + id + ", " +
			"firstName = " + firstName + ", " +
			"lastName = " + lastName + ", " +
			"address = " + address + ", " +
			"city = " + city + ", " +
			"telephone = " + telephone + ", " +
			"petIds = " + petIds + ")";
	}
}
