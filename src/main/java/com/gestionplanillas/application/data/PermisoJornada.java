package com.gestionplanillas.application.data;

import java.sql.Date;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "permiso_jornada")
@Setter @Getter
@NoArgsConstructor
public class PermisoJornada {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso_jornada")
    private Long idPermisoJornada;
    
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @Column(name = "constancia")
    private String constancia;

    @Column(name = "descripcion")
    private String descripcion;

    @OneToOne()
    @JoinColumn(name = "id_jornada_laboral")
    private JornadaLabora jornadaLaboral;
}
