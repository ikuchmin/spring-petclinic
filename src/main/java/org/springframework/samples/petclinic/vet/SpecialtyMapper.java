package org.springframework.samples.petclinic.vet;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.samples.petclinic.vet.rest.SpecialtyCrudRestDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpecialtyMapper {
	Specialty toEntity(SpecialtyCrudRestDto specialtyCrudRestDto);

	SpecialtyCrudRestDto toSpecialtyCrudRestDto(Specialty specialty);

	Specialty updateWithNull(SpecialtyCrudRestDto specialtyCrudRestDto, @MappingTarget Specialty specialty);
}
