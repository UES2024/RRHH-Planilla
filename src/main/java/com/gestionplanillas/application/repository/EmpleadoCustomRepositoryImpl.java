package com.gestionplanillas.application.repository;

import com.gestionplanillas.application.dto.EmpleadoDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmpleadoCustomRepositoryImpl implements EmpleadoCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<EmpleadoDTO> buscarEmpleados(String nombre, Long idCargo) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
            SELECT new com.gestionplanillas.application.dto.EmpleadoDTO(
                e.idEmpleado, e.nombres, e.apellidos,
                c.nombreCargo,
                to_char(ce.fechaContrato, 'DD/MM/YYYY'),
                ce.salarioBaseMensual)
            FROM Empleado e
            LEFT JOIN e.contratoEmpleado ce
            LEFT JOIN ce.cargo c
            WHERE 1 = 1
        """);

        if (nombre != null && !nombre.isBlank()) {
            sb.append(" AND LOWER(e.nombres) LIKE LOWER(CONCAT('%', :nombre, '%')) ");
        }
        if (idCargo != null) {
            sb.append(" AND c.idCargo = :idCargo ");
        }

        TypedQuery<EmpleadoDTO> query = em.createQuery(sb.toString(), EmpleadoDTO.class);

        if (nombre != null && !nombre.isBlank()) {
            query.setParameter("nombre", nombre);
        }
        if (idCargo != null) {
            query.setParameter("idCargo", idCargo);
        }

        return query.getResultList();
    }
}
