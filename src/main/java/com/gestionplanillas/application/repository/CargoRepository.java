package com.gestionplanillas.application.repository;

import com.gestionplanillas.application.data.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Long> {
    // Puedes agregar métodos personalizados si lo necesitas
}
