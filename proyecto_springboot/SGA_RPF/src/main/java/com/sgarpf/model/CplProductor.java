package com.sgarpf.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cpl_productores")
public class CplProductor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "tipo_documento", nullable = false)
    public String tipoDocumento = "NIT";

    @Column(name = "numero_documento", nullable = false, unique = true)
    public String numeroDocumento;

    @Column(nullable = false)
    public String nombre;

    public String municipio;
    public String departamento;

    @Column(name = "tipo_productor", nullable = false)
    public String tipoProductor;

    @Column(name = "base_oficial")
    public String baseOficial;

    @Column(name = "registrado_base_oficial", nullable = false)
    public Boolean registradoBaseOficial = false;

    @Column(nullable = false)
    public Boolean habilitado = false;

    @Column(name = "fecha_verificacion")
    public LocalDate fechaVerificacion;
}
