package com.sgarpf.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sgarpf.model.ProveedorLogistico;
import com.sgarpf.service.ProveedorLogisticoService;

@CrossOrigin(origins = {
        "http://localhost:8080",
        "http://127.0.0.1:8080",
        "http://localhost:8082",
        "http://127.0.0.1:8082",
        "https://sgarpf-production.up.railway.app",
        "https://dialmorx-wq.github.io"
})
@RestController
@RequestMapping("/api/proveedores-logisticos")
public class ProveedorLogisticoRestController {

    @Autowired
    private ProveedorLogisticoService proveedorLogisticoService;

    @GetMapping
    public List<ProveedorLogistico> listarProveedores() {
        return proveedorLogisticoService.listarProveedores();
    }

    @GetMapping("/{contratoSed}")
    public ProveedorLogistico obtenerProveedor(@PathVariable Integer contratoSed) {
        return proveedorLogisticoService.obtenerProveedorPorContratoSed(contratoSed);
    }

    @PostMapping
    public ProveedorLogistico guardarProveedor(@RequestBody ProveedorLogistico proveedor) {
        return proveedorLogisticoService.guardarProveedor(proveedor);
    }

    @PutMapping("/{contratoSed}")
    public ProveedorLogistico actualizarProveedor(
            @PathVariable Integer contratoSed,
            @RequestBody ProveedorLogistico proveedor) {

        proveedor.setContratoSed(contratoSed);

        return proveedorLogisticoService.guardarProveedor(proveedor);
    }

    @DeleteMapping("/{contratoSed}")
    public String eliminarProveedor(@PathVariable Integer contratoSed) {
        proveedorLogisticoService.eliminarProveedor(contratoSed);

        return "Proveedor logistico eliminado correctamente";
    }
}
