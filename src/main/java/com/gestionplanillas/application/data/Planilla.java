package com.gestionplanillas.application.data;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

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
@Getter //@Setter
@NoArgsConstructor
public class Planilla {
    /*
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
    private ArrayList<RegistroPlanilla> registrosPlanilla;*/


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planilla")
    @Setter
    private Long idPlanilla;

    @Column(name = "fecha_inicio_corte")
    @Temporal(TemporalType.DATE)
    @Setter
    private Date fecha_inicio_corte;

    @Column(name = "fecha_fin_corte")
    @Temporal(TemporalType.DATE)
    @Setter
    private Date fecha_fin_corte;

    @OneToMany(mappedBy = "planilla", cascade = CascadeType.ALL, orphanRemoval = true) // Añadir orphanRemoval
    private List<RegistroPlanilla> registrosPlanilla = new ArrayList<>();
    // NO GENERAR UN SETTER PARA registrosPlanilla.
    // En su lugar, proporciona estos métodos de ayuda para gestionar la colección:
    public void addRegistroPlanilla(RegistroPlanilla registro) {
        if (registrosPlanilla == null) { // Aunque ya la inicializamos, es una buena práctica defensiva
            registrosPlanilla = new ArrayList<>();
        }
        registrosPlanilla.add(registro);
        registro.setPlanilla(this); // CRÍTICO: Establece el lado "Many" de la relación
    }

    public void removeRegistroPlanilla(RegistroPlanilla registro) {
        if (registrosPlanilla != null) {
            registrosPlanilla.remove(registro);
            registro.setPlanilla(null); // CRÍTICO: Desvincula el lado "Many"
        }
    }
}
