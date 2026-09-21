SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS sga_rpf3
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE sga_rpf3;

CREATE TABLE IF NOT EXISTS cpl_contratos (
  id BIGINT NOT NULL AUTO_INCREMENT,
  numero_contrato VARCHAR(80) NOT NULL,
  proveedor_operador VARCHAR(180) NOT NULL,
  fecha_inicio DATE,
  fecha_fin DATE,
  valor_total_contrato DECIMAL(18,2) NOT NULL DEFAULT 0,
  valor_total_alimentos DECIMAL(18,2) NOT NULL DEFAULT 0,
  estado VARCHAR(30) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (id),
  UNIQUE KEY uk_cpl_contratos_numero (numero_contrato)
);

CREATE TABLE IF NOT EXISTS cpl_productores (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tipo_documento VARCHAR(20) NOT NULL DEFAULT 'NIT',
  numero_documento VARCHAR(40) NOT NULL,
  nombre VARCHAR(180) NOT NULL,
  municipio VARCHAR(120),
  departamento VARCHAR(120),
  tipo_productor VARCHAR(60) NOT NULL,
  base_oficial VARCHAR(180),
  registrado_base_oficial BOOLEAN NOT NULL DEFAULT FALSE,
  habilitado BOOLEAN NOT NULL DEFAULT FALSE,
  fecha_verificacion DATE,
  PRIMARY KEY (id),
  UNIQUE KEY uk_cpl_productores_documento (numero_documento)
);

CREATE TABLE IF NOT EXISTS cpl_proveedores (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nit VARCHAR(40) NOT NULL,
  nombre VARCHAR(180) NOT NULL,
  tipo_proveedor VARCHAR(80) NOT NULL,
  municipio VARCHAR(120),
  departamento VARCHAR(120),
  habilitado BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id),
  UNIQUE KEY uk_cpl_proveedores_nit (nit)
);

CREATE TABLE IF NOT EXISTS cpl_productos (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(160) NOT NULL,
  categoria VARCHAR(80),
  industrializado BOOLEAN NOT NULL DEFAULT FALSE,
  materia_prima_principal_id BIGINT,
  autorizado BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id),
  CONSTRAINT fk_cpl_producto_materia_prima
    FOREIGN KEY (materia_prima_principal_id) REFERENCES cpl_productos(id)
);

CREATE TABLE IF NOT EXISTS cpl_compras (
  id BIGINT NOT NULL AUTO_INCREMENT,
  contrato_id BIGINT NOT NULL,
  productor_id BIGINT,
  proveedor_id BIGINT,
  producto_id BIGINT NOT NULL,
  materia_prima_validada_id BIGINT,
  periodo CHAR(7) NOT NULL,
  fecha_ejecucion DATE NOT NULL,
  valor_total_factura DECIMAL(18,2) NOT NULL DEFAULT 0,
  valor_alimentos DECIMAL(18,2) NOT NULL DEFAULT 0,
  valor_alimentos_validado DECIMAL(18,2) NOT NULL DEFAULT 0,
  valor_excluido_servicios DECIMAL(18,2) NOT NULL DEFAULT 0,
  es_compra_local BOOLEAN NOT NULL DEFAULT FALSE,
  estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
  comentario_tecnico TEXT,
  creado_por VARCHAR(120),
  creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actualizado_en TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY ix_cpl_compras_periodo_estado (periodo, estado),
  CONSTRAINT fk_cpl_compras_contrato FOREIGN KEY (contrato_id) REFERENCES cpl_contratos(id),
  CONSTRAINT fk_cpl_compras_productor FOREIGN KEY (productor_id) REFERENCES cpl_productores(id),
  CONSTRAINT fk_cpl_compras_proveedor FOREIGN KEY (proveedor_id) REFERENCES cpl_proveedores(id),
  CONSTRAINT fk_cpl_compras_producto FOREIGN KEY (producto_id) REFERENCES cpl_productos(id),
  CONSTRAINT fk_cpl_compras_materia_prima FOREIGN KEY (materia_prima_validada_id) REFERENCES cpl_productos(id)
);

CREATE TABLE IF NOT EXISTS cpl_facturas (
  id BIGINT NOT NULL AUTO_INCREMENT,
  compra_id BIGINT NOT NULL,
  numero_factura VARCHAR(80) NOT NULL,
  nit_emisor VARCHAR(40) NOT NULL,
  fecha_factura DATE NOT NULL,
  valor_factura DECIMAL(18,2) NOT NULL,
  cufe VARCHAR(160),
  valida BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (id),
  UNIQUE KEY uk_cpl_factura_unica (numero_factura, nit_emisor),
  CONSTRAINT fk_cpl_facturas_compra FOREIGN KEY (compra_id) REFERENCES cpl_compras(id)
);

CREATE TABLE IF NOT EXISTS cpl_soportes (
  id BIGINT NOT NULL AUTO_INCREMENT,
  compra_id BIGINT NOT NULL,
  tipo_soporte VARCHAR(80) NOT NULL,
  nombre_archivo VARCHAR(220),
  ruta_archivo VARCHAR(500),
  obligatorio BOOLEAN NOT NULL DEFAULT TRUE,
  aprobado BOOLEAN NOT NULL DEFAULT FALSE,
  observacion TEXT,
  cargado_por VARCHAR(120),
  cargado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY ix_cpl_soportes_compra_tipo (compra_id, tipo_soporte),
  CONSTRAINT fk_cpl_soportes_compra FOREIGN KEY (compra_id) REFERENCES cpl_compras(id)
);

CREATE TABLE IF NOT EXISTS cpl_trazabilidad (
  id BIGINT NOT NULL AUTO_INCREMENT,
  compra_id BIGINT NOT NULL,
  orden INT NOT NULL,
  rol_actor VARCHAR(60) NOT NULL,
  nombre_actor VARCHAR(180) NOT NULL,
  nit_actor VARCHAR(40),
  fecha_evento DATE,
  soporte_id BIGINT,
  completa BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (id),
  CONSTRAINT fk_cpl_trazabilidad_compra FOREIGN KEY (compra_id) REFERENCES cpl_compras(id),
  CONSTRAINT fk_cpl_trazabilidad_soporte FOREIGN KEY (soporte_id) REFERENCES cpl_soportes(id)
);

CREATE TABLE IF NOT EXISTS cpl_validaciones (
  id BIGINT NOT NULL AUTO_INCREMENT,
  compra_id BIGINT NOT NULL,
  usuario VARCHAR(120) NOT NULL,
  fecha_validacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  estado_anterior VARCHAR(30),
  estado_nuevo VARCHAR(30) NOT NULL,
  documento_soporte_id BIGINT,
  evidencia VARCHAR(500),
  observacion TEXT,
  comentario_tecnico TEXT,
  PRIMARY KEY (id),
  CONSTRAINT fk_cpl_validaciones_compra FOREIGN KEY (compra_id) REFERENCES cpl_compras(id),
  CONSTRAINT fk_cpl_validaciones_soporte FOREIGN KEY (documento_soporte_id) REFERENCES cpl_soportes(id)
);

CREATE TABLE IF NOT EXISTS cpl_observaciones (
  id BIGINT NOT NULL AUTO_INCREMENT,
  compra_id BIGINT NOT NULL,
  motivo VARCHAR(80) NOT NULL,
  descripcion TEXT NOT NULL,
  estado VARCHAR(30) NOT NULL DEFAULT 'ABIERTA',
  usuario VARCHAR(120) NOT NULL,
  fecha_observacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_cierre TIMESTAMP NULL,
  PRIMARY KEY (id),
  KEY ix_cpl_observaciones_motivo_estado (motivo, estado),
  CONSTRAINT fk_cpl_observaciones_compra FOREIGN KEY (compra_id) REFERENCES cpl_compras(id)
);

CREATE TABLE IF NOT EXISTS cpl_alertas (
  id BIGINT NOT NULL AUTO_INCREMENT,
  compra_id BIGINT,
  contrato_id BIGINT,
  periodo CHAR(7),
  tipo_alerta VARCHAR(80) NOT NULL,
  mensaje TEXT NOT NULL,
  nivel VARCHAR(20) NOT NULL DEFAULT 'ROJO',
  estado VARCHAR(30) NOT NULL DEFAULT 'ABIERTA',
  creada_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_cpl_alertas_compra FOREIGN KEY (compra_id) REFERENCES cpl_compras(id),
  CONSTRAINT fk_cpl_alertas_contrato FOREIGN KEY (contrato_id) REFERENCES cpl_contratos(id)
);

CREATE TABLE IF NOT EXISTS cpl_reportes (
  id BIGINT NOT NULL AUTO_INCREMENT,
  contrato_id BIGINT NOT NULL,
  periodo CHAR(7) NOT NULL,
  tipo_reporte VARCHAR(40) NOT NULL,
  ruta_archivo VARCHAR(500),
  generado_por VARCHAR(120),
  generado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_cpl_reportes_contrato FOREIGN KEY (contrato_id) REFERENCES cpl_contratos(id)
);

CREATE OR REPLACE VIEW cpl_matriz_calculo AS
SELECT
  c.contrato_id,
  ct.numero_contrato,
  c.periodo,
  SUM(CASE WHEN c.estado = 'VALIDADA' THEN c.valor_alimentos_validado ELSE 0 END) AS valor_compras_locales_validadas,
  MAX(ct.valor_total_alimentos) AS valor_total_alimentos,
  CASE
    WHEN MAX(ct.valor_total_alimentos) = 0 THEN 0
    ELSE ROUND((SUM(CASE WHEN c.estado = 'VALIDADA' THEN c.valor_alimentos_validado ELSE 0 END) / MAX(ct.valor_total_alimentos)) * 100, 2)
  END AS porcentaje_cpl
FROM cpl_compras c
INNER JOIN cpl_contratos ct ON ct.id = c.contrato_id
GROUP BY c.contrato_id, ct.numero_contrato, c.periodo;
