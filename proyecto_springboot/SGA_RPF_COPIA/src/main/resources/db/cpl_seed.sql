SET NAMES utf8mb4;

USE sga_rpf3;

INSERT INTO cpl_contratos (
  numero_contrato,
  proveedor_operador,
  fecha_inicio,
  fecha_fin,
  valor_total_contrato,
  valor_total_alimentos,
  estado
)
SELECT 'OC_SED_CPL_DEMO_2025', 'Operador PAE Bogota Demo', '2025-01-20', '2025-12-19', 1000000000, 700000000, 'ACTIVO'
WHERE NOT EXISTS (
  SELECT 1 FROM cpl_contratos WHERE numero_contrato = 'OC_SED_CPL_DEMO_2025'
);

INSERT INTO cpl_productores (
  tipo_documento,
  numero_documento,
  nombre,
  municipio,
  departamento,
  tipo_productor,
  base_oficial,
  registrado_base_oficial,
  habilitado,
  fecha_verificacion
)
SELECT 'NIT', '900111222-1', 'COLEGA Productores Lacteos', 'Ubaté', 'Cundinamarca', 'ASOCIACION', 'Base oficial autorizada', TRUE, TRUE, CURRENT_DATE()
WHERE NOT EXISTS (
  SELECT 1 FROM cpl_productores WHERE numero_documento = '900111222-1'
);

INSERT INTO cpl_proveedores (
  nit,
  nombre,
  tipo_proveedor,
  municipio,
  departamento,
  habilitado
)
SELECT '890904478-6', 'Cooperativa COLANTA', 'Cooperativa', 'Medellin', 'Antioquia', TRUE
WHERE NOT EXISTS (
  SELECT 1 FROM cpl_proveedores WHERE nit = '890904478-6'
);

INSERT INTO cpl_productos (nombre, categoria, industrializado, materia_prima_principal_id, autorizado)
SELECT 'Leche', 'Materia prima', FALSE, NULL, TRUE
WHERE NOT EXISTS (SELECT 1 FROM cpl_productos WHERE nombre = 'Leche');

INSERT INTO cpl_productos (nombre, categoria, industrializado, materia_prima_principal_id, autorizado)
SELECT 'Yogur', 'Industrializado lacteo', TRUE, (SELECT id FROM cpl_productos WHERE nombre = 'Leche' LIMIT 1), TRUE
WHERE NOT EXISTS (SELECT 1 FROM cpl_productos WHERE nombre = 'Yogur');

INSERT INTO cpl_productos (nombre, categoria, industrializado, materia_prima_principal_id, autorizado)
SELECT 'Fruta', 'Materia prima', FALSE, NULL, TRUE
WHERE NOT EXISTS (SELECT 1 FROM cpl_productos WHERE nombre = 'Fruta');

INSERT INTO cpl_productos (nombre, categoria, industrializado, materia_prima_principal_id, autorizado)
SELECT 'Nectar', 'Industrializado fruta', TRUE, (SELECT id FROM cpl_productos WHERE nombre = 'Fruta' LIMIT 1), TRUE
WHERE NOT EXISTS (SELECT 1 FROM cpl_productos WHERE nombre = 'Nectar');

INSERT INTO cpl_compras (
  contrato_id,
  productor_id,
  proveedor_id,
  producto_id,
  materia_prima_validada_id,
  periodo,
  fecha_ejecucion,
  valor_total_factura,
  valor_alimentos,
  valor_alimentos_validado,
  valor_excluido_servicios,
  es_compra_local,
  estado,
  comentario_tecnico,
  creado_por
)
SELECT
  (SELECT id FROM cpl_contratos WHERE numero_contrato = 'OC_SED_CPL_DEMO_2025' LIMIT 1),
  (SELECT id FROM cpl_productores WHERE numero_documento = '900111222-1' LIMIT 1),
  (SELECT id FROM cpl_proveedores WHERE nit = '890904478-6' LIMIT 1),
  (SELECT id FROM cpl_productos WHERE nombre = 'Yogur' LIMIT 1),
  (SELECT id FROM cpl_productos WHERE nombre = 'Leche' LIMIT 1),
  '2025-04',
  '2025-04-15',
  250000000,
  220000000,
  220000000,
  30000000,
  TRUE,
  'VALIDADA',
  'Compra local validada sobre materia prima principal: leche.',
  'sistema'
WHERE NOT EXISTS (
  SELECT 1 FROM cpl_compras
  WHERE periodo = '2025-04'
    AND valor_total_factura = 250000000
    AND producto_id = (SELECT id FROM cpl_productos WHERE nombre = 'Yogur' LIMIT 1)
);

INSERT INTO cpl_compras (
  contrato_id,
  productor_id,
  proveedor_id,
  producto_id,
  materia_prima_validada_id,
  periodo,
  fecha_ejecucion,
  valor_total_factura,
  valor_alimentos,
  valor_alimentos_validado,
  valor_excluido_servicios,
  es_compra_local,
  estado,
  comentario_tecnico,
  creado_por
)
SELECT
  (SELECT id FROM cpl_contratos WHERE numero_contrato = 'OC_SED_CPL_DEMO_2025' LIMIT 1),
  NULL,
  (SELECT id FROM cpl_proveedores WHERE nit = '890904478-6' LIMIT 1),
  (SELECT id FROM cpl_productos WHERE nombre = 'Nectar' LIMIT 1),
  (SELECT id FROM cpl_productos WHERE nombre = 'Fruta' LIMIT 1),
  '2025-04',
  '2025-04-18',
  80000000,
  80000000,
  0,
  0,
  FALSE,
  'OBSERVADA',
  'Pendiente subsanar productor y trazabilidad.',
  'sistema'
WHERE NOT EXISTS (
  SELECT 1 FROM cpl_compras
  WHERE periodo = '2025-04'
    AND valor_total_factura = 80000000
    AND producto_id = (SELECT id FROM cpl_productos WHERE nombre = 'Nectar' LIMIT 1)
);

INSERT INTO cpl_facturas (compra_id, numero_factura, nit_emisor, fecha_factura, valor_factura, cufe, valida)
SELECT id, 'FE-CPL-001', '890904478-6', '2025-04-15', 250000000, 'CUFE-DEMO-001', TRUE
FROM cpl_compras
WHERE periodo = '2025-04'
  AND valor_total_factura = 250000000
  AND NOT EXISTS (SELECT 1 FROM cpl_facturas WHERE numero_factura = 'FE-CPL-001' AND nit_emisor = '890904478-6');

INSERT INTO cpl_trazabilidad (compra_id, orden, rol_actor, nombre_actor, nit_actor, fecha_evento, soporte_id, completa)
SELECT c.id, t.orden, t.rol_actor, t.nombre_actor, t.nit_actor, '2025-04-15', NULL, TRUE
FROM cpl_compras c
JOIN (
  SELECT 1 AS orden, 'PRODUCTOR' AS rol_actor, 'COLEGA Productores Lacteos' AS nombre_actor, '900111222-1' AS nit_actor
  UNION ALL SELECT 2, 'COOPERATIVA', 'Cooperativa COLANTA', '890904478-6'
  UNION ALL SELECT 3, 'TRANSFORMADOR', 'Cooperativa COLANTA', '890904478-6'
  UNION ALL SELECT 4, 'OPERADOR', 'Operador PAE Bogota Demo', 'N/A'
  UNION ALL SELECT 5, 'PAE', 'PAE Bogota', 'N/A'
) t
WHERE c.periodo = '2025-04'
  AND c.valor_total_factura = 250000000
  AND NOT EXISTS (
    SELECT 1 FROM cpl_trazabilidad tr WHERE tr.compra_id = c.id
  );
