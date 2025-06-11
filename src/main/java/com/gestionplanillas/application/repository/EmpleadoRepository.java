package com.gestionplanillas.application.repository;

import com.gestionplanillas.application.data.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long>, EmpleadoCustomRepository {
}
