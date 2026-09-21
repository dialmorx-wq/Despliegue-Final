package com.sgarpf.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "proveedores_logisticos")
public class ProveedorLogistico {

    @Id
    @Column(name = "contrato_sed")
    private Integer contratoSed;

    @Column(name = "contrato_cce", nullable = false, unique = true)
    private Integer contratoCce;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "tipo_proveedor", nullable = false)
    private String tipoProveedor = "Proveedor Logistico";

    @Column(nullable = false)
    private String estado = "Activo";

    public ProveedorLogistico() {
    }

    public ProveedorLogistico(Integer contratoSed, Integer contratoCce, String nombre, String tipoProveedor, String estado) {
        this.contratoSed = contratoSed;
        this.contratoCce = contratoCce;
        this.nombre = nombre;
        this.tipoProveedor = tipoProveedor;
        this.estado = estado;
    }

    public Integer getContratoSed() {
        return contratoSed;
    }

    public void setContratoSed(Integer contratoSed) {
        this.contratoSed = contratoSed;
    }

    public Integer getContratoCce() {
        return contratoCce;
    }

    public void setContratoCce(Integer contratoCce) {
        this.contratoCce = contratoCce;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoProveedor() {
        return tipoProveedor;
    }

    public void setTipoProveedor(String tipoProveedor) {
        this.tipoProveedor = tipoProveedor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
