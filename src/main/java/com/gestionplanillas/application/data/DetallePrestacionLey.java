package com.gestionplanillas.application.data;

import java.math.BigDecimal;

import jakarta.persistence.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name = "detalle_prestacion_ley")
@Getter @Setter
@NoArgsConstructor
public class DetallePrestacionLey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_prestacion_ley")
    private Long idDetallePrestacionLey;

    @Column(name = "monto")
    private BigDecimal monto;

    @ManyToOne()
    @JoinColumn(name = "id_prestacion_de_ley")
    private PrestacionDeLey prestacionDeLey;

    @ManyToOne()
    @JoinColumn(name = "id_registro_planilla")
    private RegistroPlanilla registroPlanilla;
}
