package com.sgarpf.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cpl_proveedores")
public class CplProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String nit;
    public String nombre;
    public String tipoProveedor;
    public String municipio;
    public String departamento;
    public Boolean habilitado = true;
}
