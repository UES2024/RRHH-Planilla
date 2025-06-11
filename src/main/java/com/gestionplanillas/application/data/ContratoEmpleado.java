package com.gestionplanillas.application.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Id;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contrato_empleado")
@NoArgsConstructor
@Getter @Setter
public class ContratoEmpleado {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id_contrato_empleado")
    private Long idContratoEmpleado;

    @Column(name = "salario_base_mensual")
    private BigDecimal salarioBaseMensual;

    @Column(name = "fecha_contrato")
    private LocalDateTime fechaContrato;

    @OneToOne
    @JoinColumn(name = "id_empleado", nullable = true)
    private Empleado empleado;

    @ManyToOne
    @JoinColumn(name = "id_cargo")
    private Cargo cargo;

    @OneToMany(mappedBy = "contratoEmpleado", cascade = CascadeType.ALL)
    private List<PlanificacionJornada> planificacionesJornadas;

    @OneToMany(mappedBy =  "contratoEmpleado", cascade = CascadeType.ALL)
    private List<Bonificacion> bonificaciones;

    @JoinTable(
        name = "contrato_viatico",
        joinColumns = @JoinColumn(name = "id_viatico"),
        inverseJoinColumns =  @JoinColumn(name = "id_contrato_empleado")
    )
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Viatico> viaticos;

    @OneToMany(mappedBy =  "contratoEmpleado", cascade = CascadeType.ALL)
    private List<RegistroPlanilla> listaRegistroPlanilla;
}
