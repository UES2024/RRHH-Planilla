package com.gestionplanillas.application.repository;

import com.gestionplanillas.application.data.PrestacionDeLey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PrestacionDeLeyRepository  extends JpaRepository<PrestacionDeLey, Long>, JpaSpecificationExecutor<PrestacionDeLey> {
}
