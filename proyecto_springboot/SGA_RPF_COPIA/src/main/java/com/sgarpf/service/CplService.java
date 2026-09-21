package com.sgarpf.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sgarpf.model.CplAlerta;
import com.sgarpf.model.CplCompra;
import com.sgarpf.model.CplContrato;
import com.sgarpf.model.CplFactura;
import com.sgarpf.model.CplObservacion;
import com.sgarpf.model.CplProductor;
import com.sgarpf.model.CplProducto;
import com.sgarpf.model.CplSoporte;
import com.sgarpf.model.CplTrazabilidad;
import com.sgarpf.model.CplValidacion;
import com.sgarpf.repository.CplAlertaRepository;
import com.sgarpf.repository.CplCompraRepository;
import com.sgarpf.repository.CplContratoRepository;
import com.sgarpf.repository.CplFacturaRepository;
import com.sgarpf.repository.CplObservacionRepository;
import com.sgarpf.repository.CplProductorRepository;
import com.sgarpf.repository.CplProductoRepository;
import com.sgarpf.repository.CplSoporteRepository;
import com.sgarpf.repository.CplTrazabilidadRepository;
import com.sgarpf.repository.CplValidacionRepository;

@Service
public class CplService {

    private static final BigDecimal META_CPL = new BigDecimal("30");

    @Autowired
    private CplContratoRepository contratoRepository;

    @Autowired
    private CplCompraRepository compraRepository;

    @Autowired
    private CplProductorRepository productorRepository;

    @Autowired
    private CplProductoRepository productoRepository;

    @Autowired
    private CplFacturaRepository facturaRepository;

    @Autowired
    private CplSoporteRepository soporteRepository;

    @Autowired
    private CplTrazabilidadRepository trazabilidadRepository;

    @Autowired
    private CplValidacionRepository validacionRepository;

    @Autowired
    private CplObservacionRepository observacionRepository;

    @Autowired
    private CplAlertaRepository alertaRepository;

    public Map<String, Object> calcularMatriz(Long contratoId, String periodo) {
        CplContrato contrato = contratoRepository.findById(contratoId).orElse(null);
        List<CplCompra> compras = compraRepository.findByContratoIdAndPeriodo(contratoId, periodo);

        BigDecimal valorValidado = BigDecimal.ZERO;
        int aprobadas = 0;
        int observadas = 0;
        int rechazadas = 0;
        int pendientes = 0;
        int trazabilidadIncompleta = 0;

        for (CplCompra compra : compras) {
            if ("VALIDADA".equals(compra.estado)) {
                aprobadas++;
                valorValidado = valorValidado.add(nullToZero(compra.valorAlimentosValidado));
            } else if ("OBSERVADA".equals(compra.estado)) {
                observadas++;
            } else if ("RECHAZADA".equals(compra.estado)) {
                rechazadas++;
            } else {
                pendientes++;
            }

            if (!tieneTrazabilidadCompleta(compra.id)) {
                trazabilidadIncompleta++;
            }
        }

        BigDecimal valorTotalAlimentos = contrato == null
                ? BigDecimal.ZERO
                : nullToZero(contrato.valorTotalAlimentos);
        BigDecimal porcentaje = calcularPorcentaje(valorValidado, valorTotalAlimentos);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("contratoId", contratoId);
        resultado.put("periodo", periodo);
        resultado.put("valorComprasLocalesValidadas", valorValidado);
        resultado.put("valorTotalAlimentos", valorTotalAlimentos);
        resultado.put("porcentajeCpl", porcentaje);
        resultado.put("semaforo", semaforoPorcentaje(porcentaje));
        resultado.put("comprasAprobadas", aprobadas);
        resultado.put("comprasObservadas", observadas);
        resultado.put("comprasRechazadas", rechazadas);
        resultado.put("comprasPendientes", pendientes);
        resultado.put("trazabilidadIncompleta", trazabilidadIncompleta);
        resultado.put("alertaMeta30", porcentaje.compareTo(META_CPL) < 0);

        if (porcentaje.compareTo(META_CPL) < 0) {
            crearAlerta(null, contratoId, periodo, "PORCENTAJE_MENOR_30",
                    "El porcentaje CPL del periodo esta por debajo del 30%.", "ROJO");
        }

        return resultado;
    }

    public CplValidacion validarCompra(Long compraId, CplValidacion solicitud) {
        CplCompra compra = compraRepository.findById(compraId)
                .orElseThrow(() -> new IllegalArgumentException("Compra CPL no encontrada"));

        List<String> hallazgos = obtenerHallazgos(compra);
        String estadoAnterior = compra.estado;
        String estadoSolicitado = solicitud.estadoNuevo == null ? "VALIDADA" : solicitud.estadoNuevo;

        if ("VALIDADA".equals(estadoSolicitado) && !hallazgos.isEmpty()) {
            compra.estado = "OBSERVADA";
            solicitud.estadoNuevo = "OBSERVADA";
            solicitud.observacion = unirHallazgos(hallazgos);
            for (String hallazgo : hallazgos) {
                crearObservacion(compra.id, hallazgo, hallazgo, solicitud.usuario);
            }
        } else {
            compra.estado = estadoSolicitado;
        }

        if ("VALIDADA".equals(compra.estado)) {
            compra.valorAlimentosValidado = nullToZero(compra.valorAlimentos);
            compra.esCompraLocal = true;
        }

        compraRepository.save(compra);

        solicitud.compraId = compra.id;
        solicitud.estadoAnterior = estadoAnterior;
        solicitud.estadoNuevo = compra.estado;
        return validacionRepository.save(solicitud);
    }

    public List<String> obtenerHallazgos(CplCompra compra) {
        List<String> hallazgos = new ArrayList<>();

        CplProductor productor = compra.productorId == null
                ? null
                : productorRepository.findById(compra.productorId).orElse(null);
        if (productor == null || !Boolean.TRUE.equals(productor.registradoBaseOficial)
                || !Boolean.TRUE.equals(productor.habilitado)) {
            hallazgos.add("Proveedor no registrado");
        }

        CplProducto producto = productoRepository.findById(compra.productoId).orElse(null);
        if (producto == null || !Boolean.TRUE.equals(producto.autorizado)) {
            hallazgos.add("Producto no autorizado");
        }

        List<CplFactura> facturas = facturaRepository.findByCompraId(compra.id);
        if (facturas.isEmpty()) {
            hallazgos.add("Sin factura");
        } else {
            for (CplFactura factura : facturas) {
                if (!Boolean.TRUE.equals(factura.valida)) {
                    hallazgos.add("Factura invalida");
                }
                if (facturaRepository.existsByNumeroFacturaAndNitEmisorAndCompraIdNot(
                        factura.numeroFactura, factura.nitEmisor, compra.id)) {
                    hallazgos.add("Duplicidad documental");
                }
            }
        }

        if (!tieneSoportesObligatorios(compra.id)) {
            hallazgos.add("Faltan soportes");
        }

        if (!tieneSoporteAprobado(compra.id, "SOPORTE_PAGO")) {
            hallazgos.add("Sin soporte de pago");
        }

        if (!tieneTrazabilidadCompleta(compra.id)) {
            hallazgos.add("Sin trazabilidad");
        }

        if (compra.fechaEjecucion != null && compra.fechaEjecucion.isAfter(LocalDate.now())) {
            hallazgos.add("Orden de compra futura");
        }

        if (nullToZero(compra.valorAlimentos).compareTo(nullToZero(compra.valorTotalFactura)) > 0) {
            hallazgos.add("Valor inconsistente");
        }

        return hallazgos;
    }

    public CplFactura guardarFactura(Long compraId, CplFactura factura) {
        factura.compraId = compraId;
        return facturaRepository.save(factura);
    }

    public CplSoporte guardarSoporte(Long compraId, CplSoporte soporte) {
        soporte.compraId = compraId;
        return soporteRepository.save(soporte);
    }

    public CplTrazabilidad guardarTrazabilidad(Long compraId, CplTrazabilidad trazabilidad) {
        trazabilidad.compraId = compraId;
        return trazabilidadRepository.save(trazabilidad);
    }

    public CplObservacion crearObservacion(Long compraId, String motivo, String descripcion, String usuario) {
        CplObservacion observacion = new CplObservacion();
        observacion.compraId = compraId;
        observacion.motivo = motivo;
        observacion.descripcion = descripcion;
        observacion.usuario = usuario == null ? "sistema" : usuario;
        return observacionRepository.save(observacion);
    }

    public CplAlerta crearAlerta(Long compraId, Long contratoId, String periodo, String tipo, String mensaje, String nivel) {
        CplAlerta alerta = new CplAlerta();
        alerta.compraId = compraId;
        alerta.contratoId = contratoId;
        alerta.periodo = periodo;
        alerta.tipoAlerta = tipo;
        alerta.mensaje = mensaje;
        alerta.nivel = nivel;
        return alertaRepository.save(alerta);
    }

    private boolean tieneSoportesObligatorios(Long compraId) {
        List<CplSoporte> soportes = soporteRepository.findByCompraId(compraId);
        String[] requeridos = {
                "REGISTRO_PRODUCTOR", "NIT", "FACTURA_ELECTRONICA", "ORDEN_COMPRA",
                "REMISION", "ACTA_ENTREGA", "SOPORTE_PAGO", "CERTIFICACION_TRAZABILIDAD",
                "INFORME_MENSUAL_CPL", "MATRIZ_CALCULO", "CERTIFICADOS_SANITARIOS",
                "BUSQUEDA_LOCAL"
        };

        for (String requerido : requeridos) {
            if (!tieneSoporteAprobado(soportes, requerido)) {
                return false;
            }
        }
        return true;
    }

    private boolean tieneSoporteAprobado(Long compraId, String tipoSoporte) {
        return tieneSoporteAprobado(soporteRepository.findByCompraId(compraId), tipoSoporte);
    }

    private boolean tieneSoporteAprobado(List<CplSoporte> soportes, String tipoSoporte) {
        for (CplSoporte soporte : soportes) {
            if (tipoSoporte.equals(soporte.tipoSoporte) && Boolean.TRUE.equals(soporte.aprobado)) {
                return true;
            }
        }
        return false;
    }

    private boolean tieneTrazabilidadCompleta(Long compraId) {
        List<CplTrazabilidad> trazabilidad = trazabilidadRepository.findByCompraIdOrderByOrdenAsc(compraId);
        boolean productor = false;
        boolean operador = false;
        boolean pae = false;

        for (CplTrazabilidad item : trazabilidad) {
            if (!Boolean.TRUE.equals(item.completa)) {
                continue;
            }
            if ("PRODUCTOR".equals(item.rolActor)) {
                productor = true;
            }
            if ("OPERADOR".equals(item.rolActor)) {
                operador = true;
            }
            if ("PAE".equals(item.rolActor)) {
                pae = true;
            }
        }

        return productor && operador && pae;
    }

    private BigDecimal calcularPorcentaje(BigDecimal numerador, BigDecimal denominador) {
        if (denominador == null || denominador.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return numerador.multiply(new BigDecimal("100")).divide(denominador, 2, RoundingMode.HALF_UP);
    }

    private String semaforoPorcentaje(BigDecimal porcentaje) {
        return porcentaje.compareTo(META_CPL) >= 0 ? "VERDE" : "ROJO";
    }

    private BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String unirHallazgos(List<String> hallazgos) {
        return String.join("; ", hallazgos);
    }
}
