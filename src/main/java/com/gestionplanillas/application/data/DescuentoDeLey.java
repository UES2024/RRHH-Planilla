package com.gestionplanillas.application.data;

import java.util.List;

import jakarta.persistence.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter @Setter
@Table(name = "descuento_ley")
public class DescuentoDeLey {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_descuento_ley")
    private Long idDescuentoLey;

    @Column(name = "tasa")
    private Float tasa;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "nombre")
    private String nombre;

    @OneToMany(mappedBy = "descuentoDeLey")
    private List<DetalleDescuentoLey> detallesDescuentosLey;
}
