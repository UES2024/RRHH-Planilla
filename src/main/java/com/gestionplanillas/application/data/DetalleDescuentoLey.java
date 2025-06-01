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
@Table(name = "detalle_descuento_ley")
@Getter @Setter
@NoArgsConstructor
public class DetalleDescuentoLey {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_descuento_ley")
    private Long idDetalleDescuentoLey;

    @Column(name = "monto")
    private BigDecimal monto;

    @ManyToOne()
    @JoinColumn(name = "id_descuento_ley")
    private DescuentoDeLey descuentoDeLey;

    @ManyToOne()
    @JoinColumn(name = "id_registro_planilla")
    private RegistroPlanilla registroPlanilla;
}
