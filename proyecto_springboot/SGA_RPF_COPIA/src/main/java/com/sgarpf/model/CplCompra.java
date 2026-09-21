package com.sgarpf.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cpl_compras")
public class CplCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "contrato_id", nullable = false)
    public Long contratoId;

    @Column(name = "productor_id")
    public Long productorId;

    @Column(name = "proveedor_id")
    public Long proveedorId;

    @Column(name = "producto_id", nullable = false)
    public Long productoId;

    @Column(name = "materia_prima_validada_id")
    public Long materiaPrimaValidadaId;

    public String periodo;

    @Column(name = "fecha_ejecucion", nullable = false)
    public LocalDate fechaEjecucion;

    @Column(name = "valor_total_factura", nullable = false)
    public BigDecimal valorTotalFactura = BigDecimal.ZERO;

    @Column(name = "valor_alimentos", nullable = false)
    public BigDecimal valorAlimentos = BigDecimal.ZERO;

    @Column(name = "valor_alimentos_validado", nullable = false)
    public BigDecimal valorAlimentosValidado = BigDecimal.ZERO;

    @Column(name = "valor_excluido_servicios", nullable = false)
    public BigDecimal valorExcluidoServicios = BigDecimal.ZERO;

    @Column(name = "es_compra_local", nullable = false)
    public Boolean esCompraLocal = false;

    public String estado = "PENDIENTE";

    @Column(name = "comentario_tecnico")
    public String comentarioTecnico;

    @Column(name = "creado_por")
    public String creadoPor;

    @Column(name = "creado_en", insertable = false, updatable = false)
    public LocalDateTime creadoEn;

    @Column(name = "actualizado_en", insertable = false, updatable = false)
    public LocalDateTime actualizadoEn;
}
