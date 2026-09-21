SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS sga_rpf3
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE sga_rpf3;

CREATE TABLE IF NOT EXISTS proveedores_alimentos (
  id_proveedor INT NOT NULL,
  odc_cce INT NOT NULL,
  nombre VARCHAR(180) NOT NULL,
  tipo_proveedor VARCHAR(80) NOT NULL DEFAULT 'Proveedor de Alimentos',
  estado VARCHAR(30) NOT NULL DEFAULT 'Activo',
  PRIMARY KEY (id_proveedor),
  UNIQUE KEY uk_proveedores_alimentos_odc_cce (odc_cce)
);

INSERT INTO proveedores_alimentos (odc_cce, id_proveedor, nombre, tipo_proveedor, estado) VALUES
(153098, 2117, 'UT Giganteñas 2023', 'Proveedor de Alimentos', 'Activo'),
(153099, 2122, 'UT Bapacop - Giess SED 2023', 'Proveedor de Alimentos', 'Activo'),
(153100, 2123, 'Alimentos Ricolac SAS', 'Proveedor de Alimentos', 'Activo'),
(153101, 2121, 'UT Alimentos Gamba', 'Proveedor de Alimentos', 'Activo'),
(153102, 2116, 'UT Alimentando Nueva Generación 2023', 'Proveedor de Alimentos', 'Activo'),
(153103, 2119, 'UT Porvenir De Bogota 2023', 'Proveedor de Alimentos', 'Activo'),
(153104, 2120, 'UT Alimentos Bogota 2022', 'Proveedor de Alimentos', 'Activo'),
(153106, 2109, 'AAA Nutrikids UT', 'Proveedor de Alimentos', 'Activo'),
(153107, 2113, 'Consorcio Nutriservi Panaderia 2023', 'Proveedor de Alimentos', 'Activo'),
(153109, 2111, 'Antioqueña De Helados SAS', 'Proveedor de Alimentos', 'Activo'),
(153111, 2038, 'Alimentos Pippo S.A.S', 'Proveedor de Alimentos', 'Activo'),
(153112, 2110, 'Alinnova SAS', 'Proveedor de Alimentos', 'Activo'),
(153113, 2092, 'Catering Service Deli SAS', 'Proveedor de Alimentos', 'Activo'),
(153114, 2112, 'Cerenutrir 2023 UT', 'Proveedor de Alimentos', 'Activo'),
(153115, 2027, 'Colombina S.A.', 'Proveedor de Alimentos', 'Activo'),
(153116, 2094, 'Comapan SA', 'Proveedor de Alimentos', 'Activo'),
(153117, 2048, 'Comercializadora Nutrimos S.A.', 'Proveedor de Alimentos', 'Activo'),
(153118, 2043, 'Cooperativa Colanta', 'Proveedor de Alimentos', 'Activo'),
(153119, 2022, 'Drycol S.A.S.', 'Proveedor de Alimentos', 'Activo'),
(153120, 2033, 'Industria Panificadora El Country Ltda.', 'Proveedor de Alimentos', 'Activo'),
(153121, 2050, 'C. I. Inversiones Peniel LTDA', 'Proveedor de Alimentos', 'Activo'),
(153122, 2041, 'Mountain Food S.A.S.', 'Proveedor de Alimentos', 'Activo'),
(153123, 2075, 'Trigus S.A.S.', 'Proveedor de Alimentos', 'Activo'),
(153124, 2014, 'Pulpafruit S.A.S', 'Proveedor de Alimentos', 'Activo'),
(153357, 2057, 'Industrias Normandy S.A.', 'Proveedor de Alimentos', 'Activo')
ON DUPLICATE KEY UPDATE
  odc_cce = VALUES(odc_cce),
  nombre = VALUES(nombre),
  tipo_proveedor = VALUES(tipo_proveedor),
  estado = VALUES(estado);
