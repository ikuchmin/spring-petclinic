package org.springframework.samples.petclinic.vet.rest;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * DTO for {@link org.springframework.samples.petclinic.owner.Visit}
 */
public record VisitAppropriateDto(LocalDate date, @NotBlank String description) {
}
