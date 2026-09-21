package com.sgarpf.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sgarpf.model.CplAlerta;
import com.sgarpf.model.CplCompra;
import com.sgarpf.model.CplContrato;
import com.sgarpf.model.CplFactura;
import com.sgarpf.model.CplObservacion;
import com.sgarpf.model.CplProductor;
import com.sgarpf.model.CplProducto;
import com.sgarpf.model.CplProveedor;
import com.sgarpf.model.CplSoporte;
import com.sgarpf.model.CplTrazabilidad;
import com.sgarpf.model.CplValidacion;
import com.sgarpf.repository.CplAlertaRepository;
import com.sgarpf.repository.CplCompraRepository;
import com.sgarpf.repository.CplContratoRepository;
import com.sgarpf.repository.CplObservacionRepository;
import com.sgarpf.repository.CplProductorRepository;
import com.sgarpf.repository.CplProductoRepository;
import com.sgarpf.repository.CplProveedorRepository;
import com.sgarpf.repository.CplValidacionRepository;
import com.sgarpf.service.CplService;

@CrossOrigin(origins = {
        "http://localhost:8080",
        "http://127.0.0.1:8080",
        "http://localhost:8082",
        "http://127.0.0.1:8082",
        "https://sgarpf-production.up.railway.app"
})
@RestController
@RequestMapping("/api/cpl")
public class CplRestController {

    @Autowired
    private CplContratoRepository contratoRepository;

    @Autowired
    private CplProductorRepository productorRepository;

    @Autowired
    private CplProveedorRepository proveedorRepository;

    @Autowired
    private CplProductoRepository productoRepository;

    @Autowired
    private CplCompraRepository compraRepository;

    @Autowired
    private CplValidacionRepository validacionRepository;

    @Autowired
    private CplObservacionRepository observacionRepository;

    @Autowired
    private CplAlertaRepository alertaRepository;

    @Autowired
    private CplService cplService;

    @GetMapping("/contratos")
    public List<CplContrato> listarContratos() {
        return contratoRepository.findAll();
    }

    @PostMapping("/contratos")
    public CplContrato guardarContrato(@RequestBody CplContrato contrato) {
        return contratoRepository.save(contrato);
    }

    @GetMapping("/productores")
    public List<CplProductor> listarProductores() {
        return productorRepository.findAll();
    }

    @PostMapping("/productores")
    public CplProductor guardarProductor(@RequestBody CplProductor productor) {
        return productorRepository.save(productor);
    }

    @GetMapping("/proveedores")
    public List<CplProveedor> listarProveedores() {
        return proveedorRepository.findAll();
    }

    @PostMapping("/proveedores")
    public CplProveedor guardarProveedor(@RequestBody CplProveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    @GetMapping("/productos")
    public List<CplProducto> listarProductos() {
        return productoRepository.findAll();
    }

    @PostMapping("/productos")
    public CplProducto guardarProducto(@RequestBody CplProducto producto) {
        return productoRepository.save(producto);
    }

    @GetMapping("/compras")
    public List<CplCompra> listarCompras(
            @RequestParam(required = false) Long contratoId,
            @RequestParam(required = false) String periodo) {

        if (contratoId != null && periodo != null) {
            return compraRepository.findByContratoIdAndPeriodo(contratoId, periodo);
        }
        if (periodo != null) {
            return compraRepository.findByPeriodo(periodo);
        }
        return compraRepository.findAll();
    }

    @PostMapping("/compras")
    public CplCompra guardarCompra(@RequestBody CplCompra compra) {
        return compraRepository.save(compra);
    }

    @GetMapping("/compras/{id}")
    public CplCompra obtenerCompra(@PathVariable Long id) {
        return compraRepository.findById(id).orElse(null);
    }

    @GetMapping("/compras/{id}/hallazgos")
    public List<String> obtenerHallazgos(@PathVariable Long id) {
        CplCompra compra = compraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compra CPL no encontrada"));
        return cplService.obtenerHallazgos(compra);
    }

    @PostMapping("/compras/{id}/facturas")
    public CplFactura guardarFactura(@PathVariable Long id, @RequestBody CplFactura factura) {
        return cplService.guardarFactura(id, factura);
    }

    @PostMapping("/compras/{id}/soportes")
    public CplSoporte guardarSoporte(@PathVariable Long id, @RequestBody CplSoporte soporte) {
        return cplService.guardarSoporte(id, soporte);
    }

    @PostMapping("/compras/{id}/trazabilidad")
    public CplTrazabilidad guardarTrazabilidad(@PathVariable Long id, @RequestBody CplTrazabilidad trazabilidad) {
        return cplService.guardarTrazabilidad(id, trazabilidad);
    }

    @PostMapping("/compras/{id}/validaciones")
    public CplValidacion validarCompra(@PathVariable Long id, @RequestBody CplValidacion validacion) {
        return cplService.validarCompra(id, validacion);
    }

    @PostMapping("/compras/{id}/observaciones")
    public CplObservacion crearObservacion(@PathVariable Long id, @RequestBody CplObservacion observacion) {
        return cplService.crearObservacion(id, observacion.motivo, observacion.descripcion, observacion.usuario);
    }

    @GetMapping("/compras/{id}/validaciones")
    public List<CplValidacion> listarValidaciones(@PathVariable Long id) {
        return validacionRepository.findByCompraIdOrderByIdDesc(id);
    }

    @GetMapping("/compras/{id}/observaciones")
    public List<CplObservacion> listarObservaciones(@PathVariable Long id) {
        return observacionRepository.findByCompraIdOrderByIdDesc(id);
    }

    @GetMapping("/matriz")
    public Map<String, Object> obtenerMatriz(@RequestParam Long contratoId, @RequestParam String periodo) {
        return cplService.calcularMatriz(contratoId, periodo);
    }

    @PostMapping("/calcular")
    public Map<String, Object> calcular(@RequestParam Long contratoId, @RequestParam String periodo) {
        return cplService.calcularMatriz(contratoId, periodo);
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(@RequestParam Long contratoId, @RequestParam String periodo) {
        return cplService.calcularMatriz(contratoId, periodo);
    }

    @GetMapping("/alertas")
    public List<CplAlerta> listarAlertas(@RequestParam(defaultValue = "ABIERTA") String estado) {
        return alertaRepository.findByEstadoOrderByIdDesc(estado);
    }
}
