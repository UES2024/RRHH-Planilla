package com.gestionplanillas.application.data;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Id;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "registro_planilla")
@Getter
@Setter
@NoArgsConstructor
public class RegistroPlanilla {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro_planilla")
    private Long idRegistroPlanilla;

    @Column(name = "monto_horas_extras_diurnas")
    private BigDecimal montoHoraExtrasDiurnas;

    @Column(name = "monto_horas_extras_nocturnas")
    private BigDecimal montoHorasExtrasNocturnas;

    @Column(name = "horas_extras_diurnas")
    private Double horasExtrasDiurnas;

    @Column(name = "horas_extras_nocturnas")
    private Double horasExtrasNocturnas;

    @Column(name = "aguinaldo")
    private BigDecimal aguinaldo;

    @Column(name = "vacacion")
    private BigDecimal vacacion;

    @Column(name = "renta")
    private BigDecimal renta;

    @Column(name = "isss_empleado")
    private BigDecimal isssEmpleado;

    @Column(name = "isss_patrono")
    private BigDecimal isssPatrono;

    @Column(name = "afp_empleado")
    private BigDecimal afpEmpleado;

    @Column(name = "afp_patrono")
    private BigDecimal afpPatrono;

    @Column(name = "salario_base_periodo")
    private BigDecimal salarioBasePeriodo;

    @Column(name = "total_devengado")
    private BigDecimal totalDevengado;

    @Column(name = "total_deducciones")
    private BigDecimal totalDeducciones;

    @Column(name = "salario_neto")
    private BigDecimal salarioNeto;
    @ManyToOne()
    @JoinColumn(name = "id_contrato_empleado")
    private ContratoEmpleado contratoEmpleado;

    @ManyToOne()
    @JoinColumn(name = "id_planilla")
    private Planilla planilla;


}
