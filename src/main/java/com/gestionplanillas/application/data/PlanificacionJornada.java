package com.gestionplanillas.application.data;

import java.sql.Date;


import jakarta.persistence.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "planificacion_jornada")
@NoArgsConstructor
@Getter @Setter
public class PlanificacionJornada {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planificacion_jornada")
    private Long idPlanificaionJornada;

    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;

    @Column(name = "fecha_fin")
    @Temporal(TemporalType.DATE)
    private Date fechaFin;

    @Column(name = "esta_activo")
    private Boolean estaActivo;

    @ManyToOne()
    @JoinColumn(name = "id_horario")
    private Horario horario;

    @ManyToOne()
    @JoinColumn(name = "id_contrato_empleado")
    private ContratoEmpleado contratoEmpleado;
}
