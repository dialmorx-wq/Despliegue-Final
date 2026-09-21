<template>
  <div class="ordenes-page">
    <nav class="navbar navbar-dark bg-dark px-4">
      <span class="navbar-brand">SGA_RPF</span>

      <router-link class="btn btn-outline-light btn-sm" to="/dashboard">
        <i class="bi bi-arrow-left"></i>
        Volver al dashboard
      </router-link>
    </nav>

    <main class="container-fluid py-4">
      <div class="page-header mb-4">
        <div>
          <h1>Repositorio de Ordenes de Compra</h1>
          <p>
            Gestor documental institucional para las ordenes de compra del
            Programa de Alimentacion Escolar.
          </p>
        </div>

        <div class="header-badge">
          <i class="bi bi-archive"></i>
          <span>{{ ordenesFiltradas.length }} documentos</span>
        </div>
      </div>

      <div class="search-panel shadow-sm mb-4">
        <div class="row g-2 align-items-center">
          <div class="col-md">
            <div class="input-group">
              <span class="input-group-text">
                <i class="bi bi-search"></i>
              </span>

              <input
                type="text"
                class="form-control"
                placeholder="Buscar por codigo, tipo, proveedor o estado"
                v-model="busqueda"
              />
            </div>
          </div>

          <div class="col-md-auto">
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
      </div>

      <div class="row g-4">
        <div class="col-lg-8 col-xl-9">
          <section class="mb-5">
            <div class="section-title">
              <div>
                <h2>Proveedores de Alimentos</h2>
                <span>{{ ordenesAlimentosFiltradas.length }} ordenes registradas</span>
              </div>
            </div>

            <div class="row g-3">
              <div
                v-for="orden in ordenesAlimentosFiltradas"
                :key="orden.codigo"
                class="col-sm-6 col-xl-4"
              >
                <article
                  class="order-card"
                  :class="{ active: ordenSeleccionada?.codigo === orden.codigo }"
                  @click="seleccionarOrden(orden)"
                >
                  <div class="card-icon">
                    <i class="bi bi-folder-fill"></i>
                  </div>

                  <div class="card-content">
                    <h3>{{ orden.codigo }}</h3>

                    <div class="info-row">
                      <span>Tipo:</span>
                      <strong>{{ orden.tipo }}</strong>
                    </div>

                    <div class="info-row">
                      <span>Estado:</span>
                      <strong :class="claseEstado(orden.estado)">
                        {{ orden.estado }}
                      </strong>
                    </div>

                    <button class="btn btn-outline-primary btn-sm mt-3" type="button">
                      <i class="bi bi-eye"></i>
                      Ver Detalle
                    </button>
                  </div>
                </article>
              </div>
            </div>

            <div v-if="ordenesAlimentosFiltradas.length === 0" class="empty-state">
              No se encontraron ordenes de proveedores de alimentos.
            </div>
          </section>

          <section>
            <div class="section-title">
              <div>
                <h2>Proveedores Logisticos</h2>
                <span>{{ ordenesLogisticosFiltradas.length }} ordenes registradas</span>
              </div>
            </div>

            <div class="row g-3">
              <div
                v-for="orden in ordenesLogisticosFiltradas"
                :key="orden.codigo"
                class="col-sm-6 col-xl-4"
              >
                <article
                  class="order-card logistic-card"
                  :class="{ active: ordenSeleccionada?.codigo === orden.codigo }"
                  @click="seleccionarOrden(orden)"
                >
                  <div class="card-icon">
                    <i class="bi bi-folder-fill"></i>
                  </div>

                  <div class="card-content">
                    <h3>{{ orden.codigo }}</h3>

                    <div class="info-row">
                      <span>Tipo:</span>
                      <strong>{{ orden.tipo }}</strong>
                    </div>

                    <div class="info-row">
                      <span>Estado:</span>
                      <strong :class="claseEstado(orden.estado)">
                        {{ orden.estado }}
                      </strong>
                    </div>

                    <button class="btn btn-outline-primary btn-sm mt-3" type="button">
                      <i class="bi bi-eye"></i>
                      Ver Detalle
                    </button>
                  </div>
                </article>
              </div>
            </div>

            <div v-if="ordenesLogisticosFiltradas.length === 0" class="empty-state">
              No se encontraron ordenes de proveedores logisticos.
            </div>
          </section>
        </div>

        <div class="col-lg-4 col-xl-3">
          <aside class="detail-panel shadow-sm">
            <div v-if="ordenSeleccionada">
              <div class="detail-header detail-header-actions">
                <div class="detail-title">
                  <i class="bi bi-file-earmark-text"></i>
                  <div>
                    <h2>Detalle de Orden</h2>
                    <span>Informacion documental</span>
                  </div>
                </div>

                <button
                  v-if="!modoEdicion"
                  class="btn btn-outline-primary btn-sm"
                  type="button"
                  @click="activarEdicion"
                >
                  <i class="bi bi-pencil-square"></i>
                  Editar
                </button>
              </div>

              <div v-if="mensajeEdicion" class="alert alert-success py-2">
                {{ mensajeEdicion }}
              </div>

              <div v-if="errorEdicion" class="alert alert-danger py-2">
                {{ errorEdicion }}
              </div>

              <form v-if="modoEdicion" @submit.prevent="guardarCambios">
                <div class="detail-code">
                  {{ formularioEdicion.codigo }}
                </div>

                <div class="mb-3">
                  <label class="form-label">Tipo de Proveedor</label>
                  <select class="form-select" v-model="formularioEdicion.tipo" required>
                    <option value="Proveedor de Alimentos">Proveedor de Alimentos</option>
                    <option value="Proveedor Logistico">Proveedor Logistico</option>
                  </select>
                </div>

                <div class="mb-3">
                  <label class="form-label">Estado</label>
                  <select class="form-select" v-model="formularioEdicion.estado" required>
                    <option value="Activo">Activo</option>
                    <option value="En Revision">En Revision</option>
                    <option value="Finalizado">Finalizado</option>
                    <option value="Suspendido">Suspendido</option>
                  </select>
                </div>

                <div class="mb-3">
                  <label class="form-label">Proveedor</label>
                  <input
                    type="text"
                    class="form-control"
                    v-model="formularioEdicion.proveedor"
                    required
                  />
                </div>

                <div class="mb-3">
                  <label class="form-label">Fecha de Inicio</label>
                  <input
                    type="date"
                    class="form-control"
                    v-model="formularioEdicion.fechaInicio"
                    required
                  />
                </div>

                <div class="mb-3">
                  <label class="form-label">Fecha de Finalizacion</label>
                  <input
                    type="date"
                    class="form-control"
                    v-model="formularioEdicion.fechaFinalizacion"
                    required
                  />
                </div>

                <div class="d-flex gap-2">
                  <button class="btn btn-primary flex-fill" type="submit">
                    <i class="bi bi-check2-circle"></i>
                    Guardar
                  </button>

                  <button
                    class="btn btn-outline-secondary flex-fill"
                    type="button"
                    @click="cancelarEdicion"
                  >
                    Cancelar
                  </button>
                </div>
              </form>

              <template v-else>
                <div class="detail-code">
                  {{ ordenSeleccionada.codigo }}
                </div>

                <div class="detail-list">
                  <div class="detail-item">
                    <span>Codigo de Orden</span>
                    <strong>{{ ordenSeleccionada.codigo }}</strong>
                  </div>

                  <div class="detail-item">
                    <span>Tipo de Proveedor</span>
                    <strong>{{ ordenSeleccionada.tipo }}</strong>
                  </div>

                  <div class="detail-item">
                    <span>Estado</span>
                    <strong :class="claseEstado(ordenSeleccionada.estado)">
                      {{ ordenSeleccionada.estado }}
                    </strong>
                  </div>

                  <div class="detail-item">
                    <span>Proveedor</span>
                    <strong>{{ ordenSeleccionada.proveedor }}</strong>
                  </div>

                  <div class="detail-item">
                    <span>Fecha de Inicio</span>
                    <strong>{{ ordenSeleccionada.fechaInicio }}</strong>
                  </div>

                  <div class="detail-item">
                    <span>Fecha de Finalizacion</span>
                    <strong>{{ ordenSeleccionada.fechaFinalizacion }}</strong>
                  </div>
                </div>
              </template>
            </div>

            <div v-else class="detail-empty">
              <i class="bi bi-folder2-open"></i>
              <h2>Selecciona una orden</h2>
              <p>Haz clic sobre una carpeta documental para visualizar su detalle.</p>
            </div>
          </aside>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, ref } from "vue";

const STORAGE_KEY = "sga_rpf_ordenes_compra";

// Texto de busqueda usado para filtrar dinamicamente las ordenes.
const busqueda = ref("");

// Orden activa y formulario editable del panel lateral.
const ordenSeleccionada = ref(null);
const modoEdicion = ref(false);
const mensajeEdicion = ref("");
const errorEdicion = ref("");
const formularioEdicion = ref({
  codigo: "",
  tipo: "",
  estado: "",
  proveedor: "",
  fechaInicio: "",
  fechaFinalizacion: "",
});

const codigosAlimentos = [
  "OC_SED_4207_CCE_153106_2025",
  "OC_SED_4208_CCE_153111_2025",
  "OC_SED_4209_CCE_153112_2025",
  "OC_SED_4210_CCE_153109_2025",
  "OC_SED_4211_CCE_153113_2025",
  "OC_SED_4212_CCE_153114_2025",
  "OC_SED_4213_CCE_153115_2025",
  "OC_SED_4214_CCE_153116_2025",
  "OC_SED_4215_CCE_153117_2025",
  "OC_SED_4216_CCE_153107_2025",
  "OC_SED_4217_CCE_153118_2025",
  "OC_SED_4218_CCE_153119_2025",
  "OC_SED_4219_CCE_153120_2025",
  "OC_SED_4220_CCE_153121_2025",
  "OC_SED_4221_CCE_153122_2025",
  "OC_SED_4222_CCE_153124_2025",
  "OC_SED_4223_CCE_153101_2025",
  "OC_SED_4225_CCE_153100_2025",
  "OC_SED_4226_CCE_153123_2025",
  "OC_SED_4227_CCE_153102_2025",
  "OC_SED_4228_CCE_153103_2025",
  "OC_SED_4229_CCE_153104_2025",
  "OC_SED_4230_CCE_153099_2025",
  "OC_SED_4231_CCE_153098_2025",
  "OC_SED_4238_CCE_153357_2025",
];

const codigosLogisticos = [
  "OC_SED_4312_CCE_153637_2025",
  "OC_SED_4313_CCE_153638_2025",
  "OC_SED_4314_CCE_153639_2025",
  "OC_SED_4315_CCE_153640_2025",
  "OC_SED_4316_CCE_153641_2025",
  "OC_SED_4317_CCE_153642_2025",
  "OC_SED_4318_CCE_153643_2025",
  "OC_SED_4319_CCE_153644_2025",
  "OC_SED_4320_CCE_153645_2025",
  "OC_SED_4321_CCE_153646_2025",
];

// Mock data base. Luego esta estructura se puede reemplazar por Spring Boot.
const ordenesMock = [
  ...codigosAlimentos.map((codigo, index) => ({
    codigo,
    tipo: "Proveedor de Alimentos",
    estado: "Activo",
    proveedor: `Proveedor de Alimentos ${index + 1}`,
    fechaInicio: "2025-01-20",
    fechaFinalizacion: "2025-12-19",
  })),
  ...codigosLogisticos.map((codigo, index) => ({
    codigo,
    tipo: "Proveedor Logistico",
    estado: "Activo",
    proveedor: `Proveedor Logistico ${index + 1}`,
    fechaInicio: "2025-01-20",
    fechaFinalizacion: "2025-12-19",
  })),
];

const obtenerOrdenesIniciales = () => {
  const ordenesGuardadas = localStorage.getItem(STORAGE_KEY);

  if (!ordenesGuardadas) {
    return [...ordenesMock];
  }

  try {
    return JSON.parse(ordenesGuardadas);
  } catch {
    localStorage.removeItem(STORAGE_KEY);
    return [...ordenesMock];
  }
};

const guardarOrdenesEnLocalStorage = () => {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(ordenes.value));
};

const ordenes = ref(obtenerOrdenesIniciales());

const ordenesFiltradas = computed(() => {
  const texto = busqueda.value.trim().toLowerCase();

  if (!texto) {
    return ordenes.value;
  }

  return ordenes.value.filter((orden) => {
    return (
      orden.codigo.toLowerCase().includes(texto) ||
      orden.tipo.toLowerCase().includes(texto) ||
      orden.estado.toLowerCase().includes(texto) ||
      orden.proveedor.toLowerCase().includes(texto)
    );
  });
});

const ordenesAlimentosFiltradas = computed(() => {
  return ordenesFiltradas.value.filter(
    (orden) => orden.tipo === "Proveedor de Alimentos"
  );
});

const ordenesLogisticosFiltradas = computed(() => {
  return ordenesFiltradas.value.filter(
    (orden) => orden.tipo === "Proveedor Logistico"
  );
});

const seleccionarOrden = (orden) => {
  ordenSeleccionada.value = orden;
  modoEdicion.value = false;
  mensajeEdicion.value = "";
  errorEdicion.value = "";
};

const activarEdicion = () => {
  if (!ordenSeleccionada.value) {
    return;
  }

  formularioEdicion.value = { ...ordenSeleccionada.value };
  modoEdicion.value = true;
  mensajeEdicion.value = "";
  errorEdicion.value = "";
};

const cancelarEdicion = () => {
  modoEdicion.value = false;
  errorEdicion.value = "";
};

const guardarCambios = () => {
  errorEdicion.value = "";
  mensajeEdicion.value = "";

  if (formularioEdicion.value.fechaFinalizacion < formularioEdicion.value.fechaInicio) {
    errorEdicion.value = "La fecha de finalizacion no puede ser anterior a la fecha de inicio.";
    return;
  }

  const indiceOrden = ordenes.value.findIndex(
    (orden) => orden.codigo === formularioEdicion.value.codigo
  );

  if (indiceOrden === -1) {
    errorEdicion.value = "No se encontro la orden seleccionada.";
    return;
  }

  ordenes.value[indiceOrden] = { ...formularioEdicion.value };
  guardarOrdenesEnLocalStorage();

  ordenSeleccionada.value = ordenes.value[indiceOrden];
  modoEdicion.value = false;
  mensajeEdicion.value = "Informacion de la orden actualizada correctamente.";
};

const restaurarDatosIniciales = () => {
  const confirmar = confirm("Seguro que deseas restaurar los datos iniciales?");

  if (!confirmar) {
    return;
  }

  localStorage.removeItem(STORAGE_KEY);
  ordenes.value = [...ordenesMock];
  ordenSeleccionada.value = null;
  modoEdicion.value = false;
  mensajeEdicion.value = "Datos iniciales restaurados correctamente.";
  errorEdicion.value = "";
};

const claseEstado = (estado) => {
  const clases = {
    Activo: "estado-activo",
    "En Revision": "estado-revision",
    Finalizado: "estado-finalizado",
    Suspendido: "estado-suspendido",
  };

  return clases[estado] || "estado-activo";
};
</script>

<style scoped>
.ordenes-page {
  min-height: 100vh;
  background: #f4f6f9;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
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

.header-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #ffffff;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  padding: 10px 14px;
  color: #1f2933;
  font-weight: 600;
}

.search-panel {
  background: #ffffff;
  border: 1px solid #e9ecef;
  border-radius: 10px;
  padding: 16px;
}

.section-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
}

.section-title h2 {
  margin: 0;
  color: #212529;
  font-size: 22px;
  font-weight: 700;
}

.section-title span {
  color: #6c757d;
  font-size: 14px;
}

.order-card {
  display: flex;
  gap: 14px;
  height: 100%;
  min-height: 178px;
  background: #ffffff;
  border: 1px solid #e1e5ea;
  border-left: 5px solid #0d6efd;
  border-radius: 10px;
  padding: 18px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.order-card:hover {
  transform: translateY(-4px);
  border-color: #0d6efd;
  box-shadow: 0 14px 30px rgba(15, 23, 42, 0.12);
}

.order-card.active {
  border-color: #0d6efd;
  box-shadow: 0 0 0 3px rgba(13, 110, 253, 0.14);
}

.logistic-card {
  border-left-color: #198754;
}

.logistic-card:hover,
.logistic-card.active {
  border-color: #198754;
}

.card-icon {
  color: #f0ad4e;
  font-size: 34px;
  line-height: 1;
}

.card-content {
  flex: 1;
  min-width: 0;
}

.card-content h3 {
  margin: 0 0 14px;
  color: #1f2933;
  font-size: 15px;
  font-weight: 700;
  word-break: break-word;
}

.info-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
  color: #6c757d;
  font-size: 14px;
}

.info-row strong {
  color: #212529;
  text-align: right;
}

.estado-activo {
  color: #198754 !important;
}

.estado-revision {
  color: #b58105 !important;
}

.estado-finalizado {
  color: #0d6efd !important;
}

.estado-suspendido {
  color: #dc3545 !important;
}

.detail-panel {
  position: sticky;
  top: 24px;
  background: #ffffff;
  border: 1px solid #e1e5ea;
  border-radius: 12px;
  padding: 22px;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 18px;
}

.detail-header-actions {
  align-items: flex-start;
  justify-content: space-between;
}

.detail-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-header i {
  color: #0d6efd;
  font-size: 28px;
}

.detail-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
}

.detail-header span {
  color: #6c757d;
  font-size: 13px;
}

.detail-code {
  background: #f1f5f9;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 18px;
  color: #1f2933;
  font-weight: 700;
  word-break: break-word;
}

.detail-list {
  display: grid;
  gap: 12px;
}

.detail-item {
  border-bottom: 1px solid #edf0f3;
  padding-bottom: 10px;
}

.detail-item span {
  display: block;
  margin-bottom: 3px;
  color: #6c757d;
  font-size: 13px;
}

.detail-item strong {
  color: #212529;
  font-size: 15px;
}

.detail-empty {
  padding: 34px 10px;
  text-align: center;
  color: #6c757d;
}

.detail-empty i {
  display: block;
  margin-bottom: 14px;
  color: #adb5bd;
  font-size: 44px;
}

.detail-empty h2 {
  color: #212529;
  font-size: 20px;
  font-weight: 700;
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
  .detail-panel {
    position: static;
  }
}

@media (max-width: 767px) {
  .page-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .page-header h1 {
    font-size: 26px;
  }

  .header-badge {
    width: 100%;
    justify-content: center;
  }
}
</style>
