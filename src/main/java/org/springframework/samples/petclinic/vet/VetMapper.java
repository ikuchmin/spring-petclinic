package org.springframework.samples.petclinic.vet;

import org.mapstruct.*;
import org.springframework.samples.petclinic.vet.rest.VetCrudRestDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface VetMapper {
	Vet toEntity(VetCrudRestDto vetCrudRestDto);

	@Mapping(target = "specialtyIds", expression = "java(specialtiesToSpecialtyIds(vet.getSpecialties()))")
	VetCrudRestDto toVetCrudRestDto(Vet vet);

	Vet updateWithNull(VetCrudRestDto vetCrudRestDto, @MappingTarget Vet vet);

	default List<Integer> specialtiesToSpecialtyIds(List<Specialty> specialties) {
		return specialties.stream().map(Specialty::getId).collect(Collectors.toList());
	}
}
