package com.gestionplanillas.application.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "application_user")
@Getter @Setter
@NoArgsConstructor
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long idEmpleado;
    @Column(name = "username", unique = true)
    private String username;
    @JsonIgnore
    @Column(name = "password")
    private String hashedPassword;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="usuario_roles",joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "rol")
    private Set<String> roles;
    @Column(name = "profile_picture", nullable = true)
    private String profilePicture;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL , optional = true)
    private Empleado empleado;
}
