package com.sgarpf.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cpl_alertas")
public class CplAlerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "compra_id")
    public Long compraId;

    @Column(name = "contrato_id")
    public Long contratoId;

    public String periodo;

    @Column(name = "tipo_alerta")
    public String tipoAlerta;

    public String mensaje;
    public String nivel = "ROJO";
    public String estado = "ABIERTA";

    @Column(name = "creada_en", insertable = false, updatable = false)
    public LocalDateTime creadaEn;
}
