package com.sgarpf.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "proveedores_alimentos")
public class ProveedorAlimento {

    @Id
    @Column(name = "id_proveedor")
    private Integer idProveedor;

    @Column(name = "odc_cce", nullable = false, unique = true)
    private Integer odcCce;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "tipo_proveedor", nullable = false)
    private String tipoProveedor = "Proveedor de Alimentos";

    @Column(nullable = false)
    private String estado = "Activo";

    public ProveedorAlimento() {
    }

    public ProveedorAlimento(Integer idProveedor, Integer odcCce, String nombre, String tipoProveedor, String estado) {
        this.idProveedor = idProveedor;
        this.odcCce = odcCce;
        this.nombre = nombre;
        this.tipoProveedor = tipoProveedor;
        this.estado = estado;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Integer getOdcCce() {
        return odcCce;
    }

    public void setOdcCce(Integer odcCce) {
        this.odcCce = odcCce;
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
