package com.gestionplanillas.application.repository;
import com.gestionplanillas.application.data.JornadaExtra; // Importa tu entidad JornadaExtra
import java.time.LocalDate; // Importa LocalDate para los parámetros del método
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JornadaExtraRepository extends JpaRepository<JornadaExtra, Long>{
    /**
     * Busca las jornadas extras para un contrato de empleado específico
     * dentro de un rango de fechas dado.
     *
     * @param idContratoEmpleado El ID del contrato del empleado.
     * @param fechaInicio La fecha de inicio del período (inclusive).
     * @param fechaFin La fecha de fin del período (inclusive).
     * @return Una lista de JornadaExtra que cumplen con los criterios.
     */
    List<JornadaExtra> findByJornadaLaboral_ContratoEmpleado_IdContratoEmpleadoAndFechaBetween(
            Long idContratoEmpleado, LocalDate fechaInicio, LocalDate fechaFin);

    // Si tu ID de ContratoEmpleado es de tipo int o Integer, ajusta el tipo de idContratoEmpleado.
    // Si tus entidades no tienen las relaciones (JornadaExtra -> JornadaLabora -> ContratoEmpleado)
    // correctamente configuradas, este método de Spring Data JPA no funcionará.
    // Asegúrate de haber realizado los cambios en JornadaExtra (campo 'fecha')
    // y en JornadaLabora (relación con 'ContratoEmpleado') que te he indicado antes.

}
