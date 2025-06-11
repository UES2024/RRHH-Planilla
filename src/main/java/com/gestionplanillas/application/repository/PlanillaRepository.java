package com.gestionplanillas.application.repository;

import com.gestionplanillas.application.data.Planilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanillaRepository extends JpaRepository<Planilla, Long>, JpaSpecificationExecutor<Planilla> {
    @Query("SELECT p FROM Planilla p " +
            "LEFT JOIN FETCH p.registrosPlanilla rp " +
            "LEFT JOIN FETCH rp.contratoEmpleado ce " +
            "LEFT JOIN FETCH ce.empleado e " + // Para obtener los nombres del empleado
            "WHERE p.id = :id")
    Optional<Planilla> findByIdWithDetails(@Param("id") Long id);

}
