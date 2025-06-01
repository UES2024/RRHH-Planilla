package com.gestionplanillas.application.data;

import java.time.LocalDateTime;

import jakarta.persistence.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "jornada_extra")
@Setter @Getter
@NoArgsConstructor
public class JornadaExtra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jornada_extra")
    private Long idJornadaExtra;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_jornada_laboral")
    private JornadaLabora jornadaLaboral;

     @Column(name = "hora_inicio")
    private LocalDateTime horaInicio;

    @Column(name = "hora_fin")
    private LocalDateTime hora_fin;

}
