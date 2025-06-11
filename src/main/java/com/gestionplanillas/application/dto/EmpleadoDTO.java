package com.gestionplanillas.application.dto;
import java.math.BigDecimal;

public class EmpleadoDTO {
    private Long idEmpleado;
    private String nombres;
    private String apellidos;
    private String nombreCargo;
    private String fechaContrato;
    private BigDecimal salario;

    public EmpleadoDTO(Long idEmpleado, String nombres, String apellidos,
                       String nombreCargo, String fechaContrato, BigDecimal salario) {
        this.idEmpleado = idEmpleado;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.nombreCargo = nombreCargo;
        this.fechaContrato = fechaContrato;
        this.salario = salario;
    }

    public Long getIdEmpleado() { return idEmpleado; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getNombreCargo() { return nombreCargo; }
    public String getFechaContrato() { return fechaContrato; }
    public BigDecimal getSalario() { return salario; }
}
