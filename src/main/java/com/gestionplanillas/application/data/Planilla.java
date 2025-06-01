package com.gestionplanillas.application.data;

import java.sql.Date;
import java.util.ArrayList;

import jakarta.persistence.Id;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "planilla")
@Getter @Setter
@NoArgsConstructor
public class Planilla {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planilla")
    private Long idPlanilla;

    @Column(name = "fecha_inicio_corte")
    @Temporal(TemporalType.DATE)
    private Date fecha_inicio_corte;

    @Column(name = "fecha_fin_corte")
    @Temporal(TemporalType.DATE)
    private Date fecha_fin_corte;

    @OneToMany(mappedBy =  "planilla", cascade = CascadeType.ALL)
    private ArrayList<RegistroPlanilla> registrosPlanilla;

    
}
