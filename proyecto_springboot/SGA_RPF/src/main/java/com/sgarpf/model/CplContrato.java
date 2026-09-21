package com.sgarpf.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cpl_contratos")
public class CplContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "numero_contrato", nullable = false, unique = true)
    public String numeroContrato;

    @Column(name = "proveedor_operador", nullable = false)
    public String proveedorOperador;

    @Column(name = "fecha_inicio")
    public LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    public LocalDate fechaFin;

    @Column(name = "valor_total_contrato", nullable = false)
    public BigDecimal valorTotalContrato = BigDecimal.ZERO;

    @Column(name = "valor_total_alimentos", nullable = false)
    public BigDecimal valorTotalAlimentos = BigDecimal.ZERO;

    public String estado = "ACTIVO";
}
