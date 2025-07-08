package org.springframework.samples.petclinic.owner;

import org.mapstruct.*;
import org.springframework.samples.petclinic.owner.rest.VisitCrudRestDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface VisitMapper {
	@Mapping(source = "petId", target = "pet.id")
    @Mapping(source = "vetId", target = "vet.id")
	Visit toEntity(VisitCrudRestDto visitCrudRestDto);

	@Mapping(source = "pet.id", target = "petId")
    @Mapping(source = "vet.id", target = "vetId")
	VisitCrudRestDto toVisitCrudRestDto(Visit visit);

	Visit updateWithNull(VisitCrudRestDto visitCrudRestDto, @MappingTarget Visit visit);
}
