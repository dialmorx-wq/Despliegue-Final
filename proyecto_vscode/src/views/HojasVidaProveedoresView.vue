<template>
  <div class="proveedores-page">
    <nav class="navbar navbar-dark bg-dark px-4">
      <span class="navbar-brand">SGA_RPF</span>

      <router-link class="btn btn-outline-light btn-sm" to="/dashboard">
        <i class="bi bi-arrow-left"></i>
        Volver al dashboard
      </router-link>
    </nav>

    <main class="container-fluid py-4">
      <header class="page-header mb-4">
        <div>
          <h1>Hojas de Vida de Proveedores</h1>
          <p>
            Seguimiento institucional de proveedores de alimentos del Programa
            de Alimentacion Escolar.
          </p>
        </div>

        <div class="header-badge">
          <i class="bi bi-building-check"></i>
          <span>{{ proveedoresFiltrados.length }} proveedores</span>
        </div>
      </header>

      <section class="search-panel shadow-sm mb-4">
        <div class="row g-2 align-items-center">
          <div class="col-lg">
            <div class="input-group">
              <span class="input-group-text">
                <i class="bi bi-search"></i>
              </span>
              <input
                type="text"
                class="form-control"
                placeholder="Buscar por nombre, NIT o municipio"
                v-model="busqueda"
              />
            </div>
          </div>

          <div class="col-lg-auto">
            <button
              class="btn btn-outline-secondary w-100"
              type="button"
              @click="restaurarDatosIniciales"
            >
              <i class="bi bi-arrow-counterclockwise"></i>
              Restaurar datos
            </button>
          </div>
        </div>
      </section>

      <div class="row g-4">
        <div class="col-xl-4">
          <section class="provider-list">
            <div class="section-title">
              <div>
                <h2>Proveedores registrados</h2>
                <span>{{ proveedoresFiltrados.length }} resultados</span>
              </div>
            </div>

            <div class="row g-3">
              <div
                v-for="proveedor in proveedoresFiltrados"
                :key="proveedor.id"
                class="col-md-6 col-xl-12"
              >
                <article
                  class="provider-card"
                  :class="{ active: proveedorSeleccionado?.id === proveedor.id }"
                  @click="seleccionarProveedor(proveedor)"
                >
                  <div class="provider-icon">
                    <i class="bi bi-person-vcard"></i>
                  </div>

                  <div class="provider-summary">
                    <div class="d-flex justify-content-between gap-2">
                      <h3>{{ proveedor.nombre }}</h3>
                      <span class="badge bg-success">Activo</span>
                    </div>

                    <p class="mb-1">
                      <i class="bi bi-upc-scan"></i>
                      NIT {{ proveedor.nit }}
                    </p>
                    <p class="mb-1">
                      <i class="bi bi-geo-alt"></i>
                      {{ proveedor.municipio }}, {{ proveedor.departamento }}
                    </p>

                    <button class="btn btn-outline-primary btn-sm mt-2" type="button">
                      <i class="bi bi-eye"></i>
                      Ver Detalle
                    </button>
                  </div>
                </article>
              </div>
            </div>

            <div v-if="proveedoresFiltrados.length === 0" class="empty-state">
              No se encontraron proveedores con ese criterio.
            </div>
          </section>
        </div>

        <div class="col-xl-8">
          <section v-if="proveedorSeleccionado" class="detail-shell shadow-sm">
            <div class="detail-header">
              <div>
                <span class="eyebrow">Ficha empresarial</span>
                <h2>{{ proveedorSeleccionado.nombre }}</h2>
                <p>NIT {{ proveedorSeleccionado.nit }}</p>
              </div>

              <button
                v-if="!modoEdicion"
                class="btn btn-primary"
                type="button"
                @click="activarEdicion"
              >
                <i class="bi bi-pencil-square"></i>
                Editar informacion
              </button>
            </div>

            <div v-if="mensaje" class="alert alert-success">
              {{ mensaje }}
            </div>

            <div v-if="error" class="alert alert-danger">
              {{ error }}
            </div>

            <form v-if="modoEdicion" @submit.prevent="guardarCambios">
              <div class="panel-section">
                <div class="section-title compact">
                  <h3>Informacion general</h3>
                </div>

                <div class="row g-3">
                  <div class="col-md-6">
                    <label class="form-label">Nombre del proveedor</label>
                    <input class="form-control" v-model="formulario.nombre" required />
                  </div>

                  <div class="col-md-6">
                    <label class="form-label">NIT</label>
                    <input class="form-control" v-model="formulario.nit" required />
                  </div>

                  <div class="col-md-6">
                    <label class="form-label">Representante Legal</label>
                    <input
                      class="form-control"
                      v-model="formulario.representanteLegal"
                      required
                    />
                  </div>

                  <div class="col-md-6">
                    <label class="form-label">Correo electronico</label>
                    <input
                      type="email"
                      class="form-control"
                      v-model="formulario.correo"
                      required
                    />
                  </div>

                  <div class="col-md-4">
                    <label class="form-label">Telefono</label>
                    <input class="form-control" v-model="formulario.telefono" required />
                  </div>

                  <div class="col-md-4">
                    <label class="form-label">Municipio</label>
                    <input class="form-control" v-model="formulario.municipio" required />
                  </div>

                  <div class="col-md-4">
                    <label class="form-label">Departamento</label>
                    <input
                      class="form-control"
                      v-model="formulario.departamento"
                      required
                    />
                  </div>

                  <div class="col-md-6">
                    <label class="form-label">Tipo de proveedor</label>
                    <input
                      class="form-control"
                      v-model="formulario.tipoProveedor"
                      required
                    />
                  </div>
                </div>
              </div>

              <div class="panel-section">
                <div class="section-title compact">
                  <h3>Productos ofertados</h3>
                </div>

                <label class="form-label">Productos separados por coma</label>
                <textarea
                  class="form-control"
                  rows="3"
                  v-model="productosTexto"
                  required
                ></textarea>
              </div>

              <div class="panel-section">
                <div class="section-title compact">
                  <h3>Valores certificados por mes</h3>
                </div>

                <div
                  v-for="certificacion in formulario.certificacionesMensuales"
                  :key="certificacion.mes"
                  class="row g-2 align-items-end mb-2"
                >
                  <div class="col-md-6">
                    <label class="form-label">Mes</label>
                    <input class="form-control" v-model="certificacion.mes" required />
                  </div>

                  <div class="col-md-6">
                    <label class="form-label">Valor certificado</label>
                    <input
                      type="number"
                      min="0"
                      class="form-control"
                      v-model.number="certificacion.valor"
                      required
                    />
                  </div>
                </div>
              </div>

              <div class="d-flex flex-wrap gap-2">
                <button class="btn btn-primary" type="submit">
                  <i class="bi bi-check2-circle"></i>
                  Guardar cambios
                </button>

                <button
                  class="btn btn-outline-secondary"
                  type="button"
                  @click="cancelarEdicion"
                >
                  Cancelar
                </button>
              </div>
            </form>

            <template v-else>
              <div class="kpi-grid mb-4">
                <div class="kpi-card">
                  <span>Total Certificado</span>
                  <strong>{{ formatoMoneda(totalCertificado) }}</strong>
                </div>

                <div class="kpi-card">
                  <span>Numero de Productos</span>
                  <strong>{{ proveedorSeleccionado.productos.length }}</strong>
                </div>

                <div class="kpi-card">
                  <span>Mes de Mayor Facturacion</span>
                  <strong>{{ mesMayorFacturacion }}</strong>
                </div>
              </div>

              <div class="panel-section">
                <div class="section-title compact">
                  <h3>Informacion general</h3>
                </div>

                <div class="info-grid">
                  <div class="info-item">
                    <span>Nombre del proveedor</span>
                    <strong>{{ proveedorSeleccionado.nombre }}</strong>
                  </div>
                  <div class="info-item">
                    <span>NIT</span>
                    <strong>{{ proveedorSeleccionado.nit }}</strong>
                  </div>
                  <div class="info-item">
                    <span>Representante Legal</span>
                    <strong>{{ proveedorSeleccionado.representanteLegal }}</strong>
                  </div>
                  <div class="info-item">
                    <span>Correo electronico</span>
                    <strong>{{ proveedorSeleccionado.correo }}</strong>
                  </div>
                  <div class="info-item">
                    <span>Telefono</span>
                    <strong>{{ proveedorSeleccionado.telefono }}</strong>
                  </div>
                  <div class="info-item">
                    <span>Municipio</span>
                    <strong>{{ proveedorSeleccionado.municipio }}</strong>
                  </div>
                  <div class="info-item">
                    <span>Departamento</span>
                    <strong>{{ proveedorSeleccionado.departamento }}</strong>
                  </div>
                  <div class="info-item">
                    <span>Tipo de proveedor</span>
                    <strong>{{ proveedorSeleccionado.tipoProveedor }}</strong>
                  </div>
                </div>
              </div>

              <div class="panel-section">
                <div class="section-title compact">
                  <h3>Productos ofertados</h3>
                </div>

                <div class="product-cloud">
                  <span
                    v-for="producto in proveedorSeleccionado.productos"
                    :key="producto"
                    class="product-badge"
                  >
                    {{ producto }}
                  </span>
                </div>
              </div>

              <div class="row g-4">
                <div class="col-lg-5">
                  <div class="panel-section h-100">
                    <div class="section-title compact">
                      <h3>Valores certificados por mes</h3>
                    </div>

                    <div class="table-responsive">
                      <table class="table table-sm align-middle">
                        <thead>
                          <tr>
                            <th>Mes</th>
                            <th class="text-end">Valor Certificado</th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr
                            v-for="certificacion in proveedorSeleccionado.certificacionesMensuales"
                            :key="certificacion.mes"
                          >
                            <td>{{ certificacion.mes }}</td>
                            <td class="text-end">
                              {{ formatoMoneda(certificacion.valor) }}
                            </td>
                          </tr>
                          <tr class="table-light fw-bold">
                            <td>Total Certificado</td>
                            <td class="text-end">{{ formatoMoneda(totalCertificado) }}</td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>

                <div class="col-lg-7">
                  <div class="panel-section h-100">
                    <div class="section-title compact">
                      <h3>Grafica de certificaciones</h3>
                    </div>
                    <div class="chart-box">
                      <canvas ref="graficaCanvas"></canvas>
                    </div>
                  </div>
                </div>
              </div>
            </template>
          </section>

          <section v-else class="detail-empty shadow-sm">
            <i class="bi bi-person-vcard"></i>
            <h2>Selecciona un proveedor</h2>
            <p>Elige una tarjeta para consultar la hoja de vida empresarial.</p>
          </section>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { Chart } from "chart.js/auto";
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from "vue";

const STORAGE_KEY = "sga_rpf_hojas_vida_proveedores";

const busqueda = ref("");
const proveedores = ref([]);
const proveedorSeleccionado = ref(null);
const modoEdicion = ref(false);
const mensaje = ref("");
const error = ref("");
const graficaCanvas = ref(null);
const instanciaGrafica = ref(null);

const formulario = ref({
  id: null,
  nombre: "",
  nit: "",
  representanteLegal: "",
  correo: "",
  telefono: "",
  municipio: "",
  departamento: "",
  tipoProveedor: "",
  productos: [],
  certificacionesMensuales: [],
});

const productosTexto = ref("");

const proveedoresMock = [
  {
    id: 1,
    nombre: "Agroalimentos del Norte S.A.S.",
    nit: "900123456-1",
    representanteLegal: "Claudia Marcela Rojas",
    correo: "contacto@agronorte.com",
    telefono: "315 456 7890",
    municipio: "Tunja",
    departamento: "Boyaca",
    tipoProveedor: "Proveedor de Alimentos",
    productos: ["Papa Criolla", "Papa Pastusa", "Zanahoria", "Cebolla", "Arveja"],
    certificacionesMensuales: [
      { mes: "Enero", valor: 12500000 },
      { mes: "Febrero", valor: 14200000 },
      { mes: "Marzo", valor: 11900000 },
      { mes: "Abril", valor: 15100000 },
    ],
  },
  {
    id: 2,
    nombre: "Distribuidora Campo Verde",
    nit: "901778234-5",
    representanteLegal: "Andres Felipe Molina",
    correo: "operaciones@campoverde.com",
    telefono: "310 882 4471",
    municipio: "Duitama",
    departamento: "Boyaca",
    tipoProveedor: "Proveedor de Alimentos",
    productos: ["Tomate", "Lechuga", "Frijol", "Cebolla", "Zanahoria"],
    certificacionesMensuales: [
      { mes: "Enero", valor: 9800000 },
      { mes: "Febrero", valor: 11300000 },
      { mes: "Marzo", valor: 12150000 },
      { mes: "Abril", valor: 10800000 },
    ],
  },
  {
    id: 3,
    nombre: "Lacteos y Proteinas Andinas",
    nit: "830456901-8",
    representanteLegal: "Sandra Milena Torres",
    correo: "administracion@proteinasandinas.com",
    telefono: "312 904 1188",
    municipio: "Sogamoso",
    departamento: "Boyaca",
    tipoProveedor: "Proveedor de Alimentos",
    productos: ["Huevos", "Lacteos", "Queso", "Yogurt", "Leche"],
    certificacionesMensuales: [
      { mes: "Enero", valor: 16200000 },
      { mes: "Febrero", valor: 15850000 },
      { mes: "Marzo", valor: 17100000 },
      { mes: "Abril", valor: 16500000 },
    ],
  },
  {
    id: 4,
    nombre: "Mercados Escolares Integrales",
    nit: "901112778-2",
    representanteLegal: "Jorge Enrique Vargas",
    correo: "info@mercadosintegrales.com",
    telefono: "320 778 9912",
    municipio: "Chiquinquira",
    departamento: "Boyaca",
    tipoProveedor: "Proveedor de Alimentos",
    productos: ["Frijol", "Arveja", "Lenteja", "Arroz", "Aceite"],
    certificacionesMensuales: [
      { mes: "Enero", valor: 13500000 },
      { mes: "Febrero", valor: 14700000 },
      { mes: "Marzo", valor: 13900000 },
      { mes: "Abril", valor: 15500000 },
    ],
  },
  {
    id: 5,
    nombre: "Hortalizas La Provincia",
    nit: "900884321-6",
    representanteLegal: "Paula Andrea Cardenas",
    correo: "proveedores@laprovincia.com",
    telefono: "317 334 1290",
    municipio: "Paipa",
    departamento: "Boyaca",
    tipoProveedor: "Proveedor de Alimentos",
    productos: ["Papa Criolla", "Tomate", "Lechuga", "Zanahoria", "Cebolla"],
    certificacionesMensuales: [
      { mes: "Enero", valor: 11100000 },
      { mes: "Febrero", valor: 12800000 },
      { mes: "Marzo", valor: 12400000 },
      { mes: "Abril", valor: 13250000 },
    ],
  },
];

const copiarProveedor = (proveedor) => JSON.parse(JSON.stringify(proveedor));

const cargarProveedores = () => {
  const datosGuardados = localStorage.getItem(STORAGE_KEY);

  if (!datosGuardados) {
    return proveedoresMock.map(copiarProveedor);
  }

  try {
    return JSON.parse(datosGuardados);
  } catch {
    localStorage.removeItem(STORAGE_KEY);
    return proveedoresMock.map(copiarProveedor);
  }
};

const persistirProveedores = () => {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(proveedores.value));
};

proveedores.value = cargarProveedores();

const proveedoresFiltrados = computed(() => {
  const texto = busqueda.value.trim().toLowerCase();

  if (!texto) {
    return proveedores.value;
  }

  return proveedores.value.filter((proveedor) => {
    return (
      proveedor.nombre.toLowerCase().includes(texto) ||
      proveedor.nit.toLowerCase().includes(texto) ||
      proveedor.municipio.toLowerCase().includes(texto)
    );
  });
});

const totalCertificado = computed(() => {
  if (!proveedorSeleccionado.value) {
    return 0;
  }

  return proveedorSeleccionado.value.certificacionesMensuales.reduce(
    (total, certificacion) => total + Number(certificacion.valor || 0),
    0
  );
});

const mesMayorFacturacion = computed(() => {
  if (!proveedorSeleccionado.value) {
    return "-";
  }

  const certificaciones = proveedorSeleccionado.value.certificacionesMensuales;

  if (certificaciones.length === 0) {
    return "-";
  }

  return certificaciones.reduce((mayor, actual) =>
    Number(actual.valor) > Number(mayor.valor) ? actual : mayor
  ).mes;
});

const seleccionarProveedor = (proveedor) => {
  proveedorSeleccionado.value = proveedor;
  modoEdicion.value = false;
  mensaje.value = "";
  error.value = "";
  renderizarGrafica();
};

const activarEdicion = () => {
  if (!proveedorSeleccionado.value) {
    return;
  }

  formulario.value = copiarProveedor(proveedorSeleccionado.value);
  productosTexto.value = formulario.value.productos.join(", ");
  modoEdicion.value = true;
  mensaje.value = "";
  error.value = "";
};

const cancelarEdicion = () => {
  modoEdicion.value = false;
  error.value = "";
};

const guardarCambios = () => {
  error.value = "";
  mensaje.value = "";

  const productos = productosTexto.value
    .split(",")
    .map((producto) => producto.trim())
    .filter(Boolean);

  if (productos.length === 0) {
    error.value = "Debes registrar al menos un producto ofertado.";
    return;
  }

  const indiceProveedor = proveedores.value.findIndex(
    (proveedor) => proveedor.id === formulario.value.id
  );

  if (indiceProveedor === -1) {
    error.value = "No se encontro el proveedor seleccionado.";
    return;
  }

  const proveedorActualizado = {
    ...formulario.value,
    productos,
    certificacionesMensuales: formulario.value.certificacionesMensuales.map(
      (certificacion) => ({
        mes: certificacion.mes,
        valor: Number(certificacion.valor || 0),
      })
    ),
  };

  proveedores.value[indiceProveedor] = proveedorActualizado;
  proveedorSeleccionado.value = proveedores.value[indiceProveedor];
  persistirProveedores();
  modoEdicion.value = false;
  mensaje.value = "Hoja de vida actualizada correctamente.";
  renderizarGrafica();
};

const restaurarDatosIniciales = () => {
  const confirmar = confirm("Seguro que deseas restaurar los proveedores iniciales?");

  if (!confirmar) {
    return;
  }

  localStorage.removeItem(STORAGE_KEY);
  proveedores.value = proveedoresMock.map(copiarProveedor);
  proveedorSeleccionado.value = null;
  modoEdicion.value = false;
  mensaje.value = "Datos iniciales restaurados correctamente.";
  error.value = "";
  destruirGrafica();
};

const formatoMoneda = (valor) => {
  return new Intl.NumberFormat("es-CO", {
    style: "currency",
    currency: "COP",
    maximumFractionDigits: 0,
  }).format(valor);
};

const destruirGrafica = () => {
  if (instanciaGrafica.value) {
    instanciaGrafica.value.destroy();
    instanciaGrafica.value = null;
  }
};

const renderizarGrafica = async () => {
  await nextTick();

  if (!graficaCanvas.value || !proveedorSeleccionado.value || modoEdicion.value) {
    return;
  }

  destruirGrafica();

  const certificaciones = proveedorSeleccionado.value.certificacionesMensuales;

  instanciaGrafica.value = new Chart(graficaCanvas.value, {
    type: "bar",
    data: {
      labels: certificaciones.map((certificacion) => certificacion.mes),
      datasets: [
        {
          label: "Valor certificado",
          data: certificaciones.map((certificacion) => certificacion.valor),
          backgroundColor: "#0d6efd",
          borderRadius: 6,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false,
        },
      },
      scales: {
        y: {
          ticks: {
            callback: (value) => formatoMoneda(value),
          },
        },
      },
    },
  });
};

watch(modoEdicion, (editando) => {
  if (!editando) {
    renderizarGrafica();
  }
});

onMounted(() => {
  if (proveedores.value.length > 0) {
    seleccionarProveedor(proveedores.value[0]);
  }
});

onBeforeUnmount(() => {
  destruirGrafica();
});
</script>

<style scoped>
.proveedores-page {
  min-height: 100vh;
  background: #f4f6f9;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.page-header h1 {
  margin: 0;
  color: #1f2933;
  font-size: 32px;
  font-weight: 700;
}

.page-header p {
  margin: 8px 0 0;
  color: #6c757d;
}

.header-badge,
.search-panel,
.detail-shell,
.detail-empty {
  background: #ffffff;
  border: 1px solid #e1e5ea;
  border-radius: 10px;
}

.header-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  color: #1f2933;
  font-weight: 600;
}

.search-panel {
  padding: 16px;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.section-title h2,
.section-title h3 {
  margin: 0;
  color: #212529;
  font-weight: 700;
}

.section-title h2 {
  font-size: 22px;
}

.section-title h3 {
  font-size: 18px;
}

.section-title span {
  color: #6c757d;
  font-size: 14px;
}

.compact {
  margin-bottom: 16px;
}

.provider-card {
  display: flex;
  gap: 14px;
  height: 100%;
  background: #ffffff;
  border: 1px solid #dde4ec;
  border-left: 5px solid #198754;
  border-radius: 10px;
  padding: 16px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.provider-card:hover {
  transform: translateY(-3px);
  border-color: #198754;
  box-shadow: 0 12px 26px rgba(15, 23, 42, 0.12);
}

.provider-card.active {
  border-color: #0d6efd;
  box-shadow: 0 0 0 3px rgba(13, 110, 253, 0.14);
}

.provider-icon {
  color: #0d6efd;
  font-size: 32px;
  line-height: 1;
}

.provider-summary {
  flex: 1;
  min-width: 0;
}

.provider-summary h3 {
  margin: 0 0 10px;
  color: #1f2933;
  font-size: 16px;
  font-weight: 700;
}

.provider-summary p {
  color: #5f6b76;
  font-size: 14px;
}

.detail-shell {
  padding: 24px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 20px;
}

.eyebrow {
  display: block;
  color: #0d6efd;
  font-size: 13px;
  font-weight: 700;
  text-transform: uppercase;
}

.detail-header h2 {
  margin: 2px 0;
  color: #1f2933;
  font-size: 26px;
  font-weight: 700;
}

.detail-header p {
  margin: 0;
  color: #6c757d;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.kpi-card {
  background: #f8fafc;
  border: 1px solid #e5ebf1;
  border-radius: 8px;
  padding: 16px;
}

.kpi-card span {
  display: block;
  color: #6c757d;
  font-size: 13px;
}

.kpi-card strong {
  display: block;
  margin-top: 8px;
  color: #1f2933;
  font-size: 20px;
}

.panel-section {
  background: #ffffff;
  border: 1px solid #edf0f3;
  border-radius: 8px;
  padding: 18px;
  margin-bottom: 18px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.info-item {
  border-bottom: 1px solid #edf0f3;
  padding-bottom: 10px;
}

.info-item span {
  display: block;
  color: #6c757d;
  font-size: 13px;
}

.info-item strong {
  display: block;
  margin-top: 3px;
  color: #212529;
  word-break: break-word;
}

.product-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.product-badge {
  background: #e9f5ef;
  border: 1px solid #b8dfca;
  border-radius: 999px;
  color: #146c43;
  font-size: 13px;
  font-weight: 600;
  padding: 7px 11px;
}

.chart-box {
  height: 300px;
}

.detail-empty {
  padding: 60px 24px;
  text-align: center;
  color: #6c757d;
}

.detail-empty i {
  display: block;
  margin-bottom: 14px;
  color: #adb5bd;
  font-size: 48px;
}

.empty-state {
  background: #ffffff;
  border: 1px dashed #ced4da;
  border-radius: 10px;
  padding: 22px;
  color: #6c757d;
  text-align: center;
}

@media (max-width: 991px) {
  .page-header,
  .detail-header {
    flex-direction: column;
  }

  .kpi-grid,
  .info-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 767px) {
  .page-header h1 {
    font-size: 26px;
  }

  .header-badge {
    width: 100%;
    justify-content: center;
  }

  .detail-shell {
    padding: 18px;
  }
}
</style>
