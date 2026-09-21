package com.sgarpf.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sgarpf.model.ProveedorAlimento;
import com.sgarpf.repository.ProveedorAlimentoRepository;

@Service
public class ProveedorAlimentoService {

    @Autowired
    private ProveedorAlimentoRepository proveedorAlimentoRepository;

    public List<ProveedorAlimento> listarProveedores() {
        return proveedorAlimentoRepository.findAll();
    }

    public ProveedorAlimento obtenerProveedorPorId(Integer idProveedor) {
        return proveedorAlimentoRepository.findById(idProveedor).orElse(null);
    }

    public ProveedorAlimento guardarProveedor(ProveedorAlimento proveedor) {
        return proveedorAlimentoRepository.save(proveedor);
    }

    public void eliminarProveedor(Integer idProveedor) {
        proveedorAlimentoRepository.deleteById(idProveedor);
    }
}
