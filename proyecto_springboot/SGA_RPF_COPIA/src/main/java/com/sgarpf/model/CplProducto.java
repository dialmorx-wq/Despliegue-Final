package com.sgarpf.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cpl_productos")
public class CplProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String nombre;
    public String categoria;
    public Boolean industrializado = false;

    @Column(name = "materia_prima_principal_id")
    public Long materiaPrimaPrincipalId;

    public Boolean autorizado = true;
}
