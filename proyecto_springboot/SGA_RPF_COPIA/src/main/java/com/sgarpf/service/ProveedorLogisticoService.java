package com.sgarpf.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sgarpf.model.ProveedorLogistico;
import com.sgarpf.repository.ProveedorLogisticoRepository;

@Service
public class ProveedorLogisticoService {

    @Autowired
    private ProveedorLogisticoRepository proveedorLogisticoRepository;

    public List<ProveedorLogistico> listarProveedores() {
        return proveedorLogisticoRepository.findAll();
    }

    public ProveedorLogistico obtenerProveedorPorContratoSed(Integer contratoSed) {
        return proveedorLogisticoRepository.findById(contratoSed).orElse(null);
    }

    public ProveedorLogistico guardarProveedor(ProveedorLogistico proveedor) {
        return proveedorLogisticoRepository.save(proveedor);
    }

    public void eliminarProveedor(Integer contratoSed) {
        proveedorLogisticoRepository.deleteById(contratoSed);
    }
}
