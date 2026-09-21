package com.sgarpf.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sgarpf.model.HojaVidaProveedor;
import com.sgarpf.repository.HojaVidaProveedorRepository;

@CrossOrigin(origins = {
        "http://localhost:8080",
        "http://127.0.0.1:8080",
        "http://localhost:8082",
        "http://127.0.0.1:8082"
})
@RestController
@RequestMapping("/api/hojas-vida-proveedores")
public class HojaVidaProveedorRestController {

    @Autowired
    private HojaVidaProveedorRepository hojaVidaProveedorRepository;

    @GetMapping
    public List<HojaVidaProveedor> listarHojasVida() {
        return hojaVidaProveedorRepository.findAll();
    }
}
