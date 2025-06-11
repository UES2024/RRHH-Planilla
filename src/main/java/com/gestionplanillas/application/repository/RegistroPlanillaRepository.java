package com.gestionplanillas.application.repository;

import com.gestionplanillas.application.data.RegistroPlanilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroPlanillaRepository extends JpaRepository<RegistroPlanilla, Long>, JpaSpecificationExecutor<RegistroPlanilla> {
}
