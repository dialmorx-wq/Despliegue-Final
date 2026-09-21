package com.sgarpf.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cpl_trazabilidad")
public class CplTrazabilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "compra_id", nullable = false)
    public Long compraId;

    public Integer orden;

    @Column(name = "rol_actor", nullable = false)
    public String rolActor;

    @Column(name = "nombre_actor", nullable = false)
    public String nombreActor;

    @Column(name = "nit_actor")
    public String nitActor;

    @Column(name = "fecha_evento")
    public LocalDate fechaEvento;

    @Column(name = "soporte_id")
    public Long soporteId;

    public Boolean completa = false;
}
