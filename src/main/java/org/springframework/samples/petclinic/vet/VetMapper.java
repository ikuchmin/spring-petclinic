package org.springframework.samples.petclinic.vet;

import org.mapstruct.*;
import org.springframework.samples.petclinic.vet.rest.VetCrudRestDto;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface VetMapper {
	Vet toEntity(VetCrudRestDto vetCrudRestDto);

	@Mapping(target = "specialtyIds", expression = "java(specialtiesToSpecialtyIds(vet.getSpecialties()))")
	VetCrudRestDto toVetCrudRestDto(Vet vet);

	Vet updateWithNull(VetCrudRestDto vetCrudRestDto, @MappingTarget Vet vet);

	default Set<Integer> specialtiesToSpecialtyIds(Set<Specialty> specialties) {
		return specialties.stream().map(Specialty::getId).collect(Collectors.toSet());
	}
}
