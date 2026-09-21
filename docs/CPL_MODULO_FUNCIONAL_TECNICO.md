# Modulo Compras Publicas Locales (CPL)

Sistema: SGA_RPF - Interventoria PAE Bogota

## Marco funcional obligatorio

El modulo CPL controla, valida y audita el cumplimiento de Compras Publicas Locales en contratos de suministro de alimentos del PAE Bogota. Todas las reglas futuras del sistema deben mantener como base:

- Ley 2046 de 2020.
- Decreto 248 de 2021.
- Lineamientos UApA.
- Anexo Tecnico SED Bogota proceso CCENEG-071-01-2022.
- Resolucion 335 de 2021.
- Lineamientos ECPA-DC cuando apliquen.

Nota: este documento traduce el marco normativo dado por el usuario a reglas funcionales y tecnicas del sistema. Antes de liberacion productiva debe validarse contra los documentos oficiales anexos del contrato aplicable.

## Regla central de calculo

El porcentaje CPL siempre se calcula asi:

```text
Porcentaje CPL = (Valor compras locales validadas / Valor total compras de alimentos) x 100
```

Reglas no negociables:

- El denominador es el valor total de compras de alimentos.
- No se usa el valor total del contrato.
- No incluye transporte, administracion, personal, servicios logisticos ni otros costos no alimentarios.
- Solo suma compras ejecutadas, soportadas y validadas.
- No se incluyen ordenes de compra futuras, proyectadas o sin ejecucion.
- La meta minima de alerta operativa es 30%.

## Estados de compra CPL

- `PENDIENTE`: registrada sin validacion final.
- `VALIDADA`: cumple reglas, soportes y trazabilidad.
- `OBSERVADA`: tiene hallazgos subsanables.
- `RECHAZADA`: no cumple o no es elegible.
- `SUBSANADA`: tuvo observacion y se aporto correccion documental.

## Semaforo UI

- Verde: compra validada o porcentaje igual/superior al 30%.
- Amarillo: compra observada, subsanada pendiente de cierre o trazabilidad parcial.
- Rojo: compra rechazada, faltan soportes criticos o porcentaje menor al 30%.

## Compra valida para CPL

Una compra solo cuenta para CPL cuando cumple todo lo siguiente:

- Productor existe en base oficial autorizada.
- Productor clasificado como pequeno productor, productor ACFC o asociacion habilitada.
- Producto o materia prima principal autorizada.
- Factura electronica valida y no duplicada.
- Trazabilidad completa.
- Soportes obligatorios cargados y aprobados.
- Valor de alimento consistente con factura, remision y matriz.
- Compra efectivamente ejecutada dentro del periodo evaluado.

## Productos industrializados

Para productos industrializados no se valida el producto terminado completo. Se valida la materia prima principal:

| Producto terminado | Materia prima CPL |
| --- | --- |
| Yogur | Leche |
| Kumis | Leche |
| Queso | Leche |
| Nectar | Fruta |
| Gelatina de pata | Panela |
| Bocadillo | Guayaba |

Regla tecnica: la compra debe registrar `producto_id` y, si aplica, `materia_prima_principal_id`. El calculo CPL usa la materia prima principal autorizada.

## Flujo de trazabilidad

Flujo esperado:

```text
Productor -> Cooperativa -> Transformador -> Operador -> PAE
```

Ejemplo:

```text
COLEGA -> COLANTA -> Yogur -> Operador -> PAE
```

Una trazabilidad completa debe identificar actor, NIT/documento, rol, fecha, soporte asociado y orden secuencial.

## Soportes obligatorios

Los soportes exigibles son:

- Registro productor.
- NIT.
- Factura electronica.
- Orden de compra.
- Remision.
- Acta de entrega.
- Soporte de pago.
- Certificacion de trazabilidad.
- Informe mensual CPL.
- Matriz de calculo.
- Certificados sanitarios.
- Correos o cotizaciones de busqueda local.

El sistema debe permitir parametrizar obligatoriedad por tipo de compra, producto, contrato y periodo.

## Motivos de observacion

- Proveedor no registrado.
- NIT inconsistente.
- Producto no autorizado.
- Sin factura.
- Sin trazabilidad.
- Sin soporte de pago.
- Valor inconsistente.
- Duplicidad documental.
- Porcentaje mal calculado.

## Alertas automaticas

El sistema debe generar alertas cuando:

- Porcentaje mensual o acumulado sea menor al 30%.
- Proveedor/productor no exista en base autorizada.
- Producto o materia prima no este autorizado.
- Falten soportes obligatorios.
- Factura este duplicada.
- Trazabilidad este incompleta.
- Valor de alimento sea mayor al valor facturado o incompatible con soportes.

## Indicadores del dashboard

- Porcentaje CPL mensual.
- Porcentaje CPL acumulado.
- Compras aprobadas.
- Compras observadas.
- Compras rechazadas.
- Proveedores activos.
- Alertas por debajo del 30%.
- Tendencia mensual.
- Trazabilidad incompleta.

## Filtros requeridos

- Mes.
- Contrato.
- Proveedor.
- Productor.
- Producto.
- Municipio.
- Estado.
- Porcentaje CPL.

## Modulos internos

- Dashboard ejecutivo CPL.
- Gestion de productores.
- Gestion de proveedores.
- Gestion de productos y materias primas.
- Registro de compras CPL.
- Carga documental.
- Validacion documental.
- Matriz CPL.
- Calculo automatico.
- Alertas.
- Reportes PDF y Excel.
- Auditoria historica.
- Observaciones y hallazgos.

## Reglas de auditoria

Toda validacion debe guardar:

- Usuario.
- Fecha y hora.
- Documento soporte.
- Observacion.
- Estado anterior y nuevo estado.
- Evidencia.
- Comentario tecnico.
- Motivo normativo o tecnico.

Ninguna validacion se debe sobrescribir sin registro historico.

## API propuesta

Base path: `/api/cpl`

Endpoints principales:

- `GET /dashboard?contratoId=&periodo=`
- `GET /productores`
- `POST /productores`
- `GET /proveedores`
- `POST /proveedores`
- `GET /productos`
- `POST /compras`
- `GET /compras?mes=&contrato=&estado=`
- `GET /compras/{id}`
- `POST /compras/{id}/soportes`
- `POST /compras/{id}/trazabilidad`
- `POST /compras/{id}/validaciones`
- `POST /compras/{id}/observaciones`
- `GET /matriz?contratoId=&periodo=`
- `POST /calcular?contratoId=&periodo=`
- `GET /alertas`
- `GET /reportes/pdf?contratoId=&periodo=`
- `GET /reportes/excel?contratoId=&periodo=`

## Formula tecnica de calculo

Compra suma como numerador si:

```text
estado = VALIDADA
AND fecha_ejecucion <= fecha_corte
AND fecha_ejecucion BETWEEN periodo_inicio AND periodo_fin
AND es_compra_local = true
AND valor_alimentos_validado > 0
AND soportes_obligatorios_aprobados = true
AND trazabilidad_completa = true
AND factura_valida = true
AND producto_autorizado = true
AND productor_habilitado = true
```

Denominador:

```text
SUM(valor_total_alimentos)
```

Por contrato y periodo, excluyendo transporte, administracion, personal y demas servicios no alimentarios.

## Criterios de aceptacion

- El sistema no permite marcar `VALIDADA` una compra sin soportes obligatorios.
- El sistema no cuenta compras `PENDIENTE`, `OBSERVADA`, `RECHAZADA` ni futuras en el porcentaje.
- El sistema detecta factura duplicada por numero, NIT proveedor y valor.
- El sistema permite observaciones tecnicas por motivo.
- El sistema conserva auditoria de cada cambio de estado.
- El dashboard alerta en rojo cuando el porcentaje CPL esta por debajo del 30%.
- Las hojas de vida de proveedores deben incluir alimentos y logisticos cuando aplique, pero el calculo CPL solo usa compras de alimentos elegibles.
