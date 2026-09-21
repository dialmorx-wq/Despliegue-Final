package com.sgarpf.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cpl_observaciones")
public class CplObservacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "compra_id", nullable = false)
    public Long compraId;

    public String motivo;
    public String descripcion;
    public String estado = "ABIERTA";
    public String usuario;

    @Column(name = "fecha_observacion", insertable = false, updatable = false)
    public LocalDateTime fechaObservacion;

    @Column(name = "fecha_cierre")
    public LocalDateTime fechaCierre;
}
