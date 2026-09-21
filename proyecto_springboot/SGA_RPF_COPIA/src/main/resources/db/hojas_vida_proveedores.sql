SET NAMES utf8mb4;

USE sga_rpf3;

CREATE OR REPLACE VIEW hojas_vida_proveedores AS
SELECT
  CONCAT('ALIMENTO-', id_proveedor) AS id,
  id_proveedor AS codigo_proveedor,
  odc_cce AS codigo_cce,
  NULL AS contrato_sed,
  nombre,
  tipo_proveedor,
  estado
FROM proveedores_alimentos
UNION ALL
SELECT
  CONCAT('LOGISTICO-', contrato_sed) AS id,
  contrato_sed AS codigo_proveedor,
  contrato_cce AS codigo_cce,
  contrato_sed AS contrato_sed,
  nombre,
  tipo_proveedor,
  estado
FROM proveedores_logisticos;
