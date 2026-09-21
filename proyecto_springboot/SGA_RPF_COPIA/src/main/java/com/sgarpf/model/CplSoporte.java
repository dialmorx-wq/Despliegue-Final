package com.sgarpf.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cpl_soportes")
public class CplSoporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "compra_id", nullable = false)
    public Long compraId;

    @Column(name = "tipo_soporte", nullable = false)
    public String tipoSoporte;

    @Column(name = "nombre_archivo")
    public String nombreArchivo;

    @Column(name = "ruta_archivo")
    public String rutaArchivo;

    public Boolean obligatorio = true;
    public Boolean aprobado = false;
    public String observacion;

    @Column(name = "cargado_por")
    public String cargadoPor;

    @Column(name = "cargado_en", insertable = false, updatable = false)
    public LocalDateTime cargadoEn;
}
