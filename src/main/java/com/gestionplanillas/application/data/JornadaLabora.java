package com.gestionplanillas.application.data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "jornada_laboral")
@Setter @Getter
@NoArgsConstructor
public class JornadaLabora {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jornada_laboral")
    private Long idJornadaLaboral;

    @ManyToOne // Una jornada laboral pertenece a UN contrato de empleado
    @JoinColumn(name = "id_contrato_empleado", nullable = false) // Columna de la FK
    private ContratoEmpleado contratoEmpleado;

    @Column(name = "hora_inicio")
    private LocalDateTime horaInicio;


    @Column(name = "hora_fin")
    private LocalDateTime hora_fin;

    @Column(name = "asistio")
    private Boolean asistio;
    
    @OneToOne(mappedBy = "jornadaLaboral", optional = 	true)
    private JornadaExtra jornadaExtra;

    @OneToOne(mappedBy = "jornadaLaboral" , optional = true)
    private PermisoJornada permisoJornada;
}
