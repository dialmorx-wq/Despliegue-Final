package com.sgarpf.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cpl_validaciones")
public class CplValidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "compra_id", nullable = false)
    public Long compraId;

    public String usuario;

    @Column(name = "fecha_validacion", insertable = false, updatable = false)
    public LocalDateTime fechaValidacion;

    @Column(name = "estado_anterior")
    public String estadoAnterior;

    @Column(name = "estado_nuevo")
    public String estadoNuevo;

    @Column(name = "documento_soporte_id")
    public Long documentoSoporteId;

    public String evidencia;
    public String observacion;

    @Column(name = "comentario_tecnico")
    public String comentarioTecnico;
}
