package com.gestionplanillas.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "horario")
@NoArgsConstructor
@Getter @Setter
public class Horario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario")
    private Long idHorario;
    @Column(name = "hora_entrada")
    private String horaEntrada;
    @Column(name="hora_salida")
    private String horaSalida;
    @Column(name = "nombre")
    private String nombre;

    @OneToMany(mappedBy  = "horario" , cascade = CascadeType.ALL)
    private List<PlanificacionJornada> planificacionesJornadas;
}
