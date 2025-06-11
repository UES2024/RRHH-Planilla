package com.gestionplanillas.application.repository;

import com.gestionplanillas.application.dto.EmpleadoDTO;
import java.util.List;

public interface EmpleadoCustomRepository {
    List<EmpleadoDTO> buscarEmpleados(String nombre, Long idCargo);
}
