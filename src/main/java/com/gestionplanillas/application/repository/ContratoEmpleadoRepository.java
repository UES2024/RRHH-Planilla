package com.gestionplanillas.application.repository;

import com.gestionplanillas.application.data.ContratoEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContratoEmpleadoRepository extends
        JpaRepository<ContratoEmpleado, Long>, JpaSpecificationExecutor<ContratoEmpleado> {
    // En ContratoEmpleadoRepository.java
    @Query("SELECT ce FROM ContratoEmpleado ce LEFT JOIN FETCH ce.bonificaciones b LEFT JOIN FETCH ce.viaticos v")
    List<ContratoEmpleado> findAllWithBonificacionesAndViaticos();
}
