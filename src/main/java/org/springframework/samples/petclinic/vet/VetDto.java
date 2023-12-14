package org.springframework.samples.petclinic.vet;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link Vet}
 */
public record VetDto(Integer id, @NotBlank String firstName, @NotBlank String lastName,
					 Set<SpecialtyDto> specialties) implements Serializable {
	/**
	 * DTO for {@link Specialty}
	 */
	public record SpecialtyDto(Integer id, String name) implements Serializable {
	}
}
