SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS sga_rpf3
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE sga_rpf3;

CREATE TABLE IF NOT EXISTS proveedores_logisticos (
  contrato_sed INT NOT NULL,
  contrato_cce INT NOT NULL,
  nombre VARCHAR(180) NOT NULL,
  tipo_proveedor VARCHAR(80) NOT NULL DEFAULT 'Proveedor Logistico',
  estado VARCHAR(30) NOT NULL DEFAULT 'Activo',
  PRIMARY KEY (contrato_sed),
  UNIQUE KEY uk_proveedores_logisticos_contrato_cce (contrato_cce)
);

INSERT INTO proveedores_logisticos (contrato_sed, contrato_cce, nombre, tipo_proveedor, estado) VALUES
(4316, 153641, 'UT SOCIAL FOO', 'Proveedor Logistico', 'Activo'),
(4319, 153644, 'LÁCTEOS APPE', 'Proveedor Logistico', 'Activo'),
(4315, 153640, 'LÁCTEOS APPE', 'Proveedor Logistico', 'Activo'),
(4313, 153638, 'UT NUTRIENDO', 'Proveedor Logistico', 'Activo'),
(4318, 153643, 'UT HBUCOL M', 'Proveedor Logistico', 'Activo'),
(4321, 153646, 'UT SOCIAL FOO', 'Proveedor Logistico', 'Activo'),
(4317, 153642, 'CONSORCIO N', 'Proveedor Logistico', 'Activo'),
(4312, 153637, 'ALIMENTOS PR', 'Proveedor Logistico', 'Activo')
ON DUPLICATE KEY UPDATE
  contrato_cce = VALUES(contrato_cce),
  nombre = VALUES(nombre),
  tipo_proveedor = VALUES(tipo_proveedor),
  estado = VALUES(estado);
