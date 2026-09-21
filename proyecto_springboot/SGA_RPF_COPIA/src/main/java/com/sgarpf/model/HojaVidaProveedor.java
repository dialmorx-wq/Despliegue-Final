package com.sgarpf.model;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "hojas_vida_proveedores")
public class HojaVidaProveedor {

    @Id
    private String id;

    @Column(name = "codigo_proveedor")
    private Integer codigoProveedor;

    @Column(name = "codigo_cce")
    private Integer codigoCce;

    @Column(name = "contrato_sed")
    private Integer contratoSed;

    private String nombre;

    @Column(name = "tipo_proveedor")
    private String tipoProveedor;

    private String estado;

    public String getId() {
        return id;
    }

    public Integer getCodigoProveedor() {
        return codigoProveedor;
    }

    public Integer getCodigoCce() {
        return codigoCce;
    }

    public Integer getContratoSed() {
        return contratoSed;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipoProveedor() {
        return tipoProveedor;
    }

    public String getEstado() {
        return estado;
    }
}
