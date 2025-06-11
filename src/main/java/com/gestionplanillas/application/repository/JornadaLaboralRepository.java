package com.gestionplanillas.application.repository;

import com.gestionplanillas.application.data.JornadaLabora; // Importa tu entidad JornadaLabora
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JornadaLaboralRepository extends JpaRepository<JornadaLabora, Long>{

}
