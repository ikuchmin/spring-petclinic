package org.springframework.samples.petclinic.owner;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Integer>, JpaSpecificationExecutor<Visit> {
	List<Visit> findByPet(Pet pet);

	@EntityGraph(attributePaths = {"vet"}, type = EntityGraph.EntityGraphType.LOAD)
	List<Visit> findWithVetByPet(Pet pet);
}
