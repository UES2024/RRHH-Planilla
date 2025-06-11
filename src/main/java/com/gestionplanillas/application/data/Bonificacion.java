package com.gestionplanillas.application.data;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.ManyToAny;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bonificacion")
@Setter @Getter
@NoArgsConstructor
public class Bonificacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bonificacion")
    private Long idBonificacion;

    @Column(name = "monto")
    private BigDecimal monto;

    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "fecha", nullable = false) // Es importante que tenga una fecha para el filtro
    private LocalDate fecha;

    @ManyToOne()
    @JoinColumn(name = "id_contrato_empleado")
    private ContratoEmpleado contratoEmpleado;

}
