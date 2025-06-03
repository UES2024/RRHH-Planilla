package com.gestionplanillas.application.data;

import java.util.List;

import jakarta.persistence.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Table(name = "prestacion_de_ley")
@Getter @Setter
public class PrestacionDeLey {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestacion_de_ley")
    private Long idPrestacionDeLey;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "regla_prestacion_ley", columnDefinition = "JSON")
    private String reglaPrestacionLey;

}
