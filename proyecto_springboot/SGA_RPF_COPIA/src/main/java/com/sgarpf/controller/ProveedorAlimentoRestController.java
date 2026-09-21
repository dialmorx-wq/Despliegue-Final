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

import com.sgarpf.model.ProveedorAlimento;
import com.sgarpf.service.ProveedorAlimentoService;

@CrossOrigin(origins = {
        "http://localhost:8080",
        "http://127.0.0.1:8080",
        "http://localhost:8082",
        "http://127.0.0.1:8082",
        "https://sgarpf-production.up.railway.app"
})
@RestController
@RequestMapping("/api/proveedores-alimentos")
public class ProveedorAlimentoRestController {

    @Autowired
    private ProveedorAlimentoService proveedorAlimentoService;

    @GetMapping
    public List<ProveedorAlimento> listarProveedores() {
        return proveedorAlimentoService.listarProveedores();
    }

    @GetMapping("/{idProveedor}")
    public ProveedorAlimento obtenerProveedor(@PathVariable Integer idProveedor) {
        return proveedorAlimentoService.obtenerProveedorPorId(idProveedor);
    }

    @PostMapping
    public ProveedorAlimento guardarProveedor(@RequestBody ProveedorAlimento proveedor) {
        return proveedorAlimentoService.guardarProveedor(proveedor);
    }

    @PutMapping("/{idProveedor}")
    public ProveedorAlimento actualizarProveedor(
            @PathVariable Integer idProveedor,
            @RequestBody ProveedorAlimento proveedor) {

        proveedor.setIdProveedor(idProveedor);

        return proveedorAlimentoService.guardarProveedor(proveedor);
    }

    @DeleteMapping("/{idProveedor}")
    public String eliminarProveedor(@PathVariable Integer idProveedor) {
        proveedorAlimentoService.eliminarProveedor(idProveedor);

        return "Proveedor eliminado correctamente";
    }
}
