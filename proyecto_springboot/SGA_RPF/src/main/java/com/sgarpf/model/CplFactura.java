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
@Table(name = "cpl_facturas")
public class CplFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "compra_id", nullable = false)
    public Long compraId;

    @Column(name = "numero_factura", nullable = false)
    public String numeroFactura;

    @Column(name = "nit_emisor", nullable = false)
    public String nitEmisor;

    @Column(name = "fecha_factura", nullable = false)
    public LocalDate fechaFactura;

    @Column(name = "valor_factura", nullable = false)
    public BigDecimal valorFactura = BigDecimal.ZERO;

    public String cufe;
    public Boolean valida = false;
}
