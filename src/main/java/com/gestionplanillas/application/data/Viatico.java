package com.gestionplanillas.application.data;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "viatico")
@Setter @Getter
@NoArgsConstructor
public class Viatico {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id_viatico")
    private Long idViaticos;

    @Column(name = "monto")
    private BigDecimal monto;

    @Column(name  = "descripcion")
    private String descripcion;

    @ManyToMany(mappedBy =  "viaticos")
    private List<ContratoEmpleado> contratosEmpleados;
    
    
}
