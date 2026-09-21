<template>
  <div class="container-fluid">
    <nav class="navbar navbar-dark bg-dark px-4">
      <span class="navbar-brand">SGA_RPF</span>

      <div class="d-flex align-items-center gap-3">
        <span class="text-white small">
          {{ usuarioLogueado ? usuarioLogueado.nombre : "Sesion activa" }}
          <span v-if="usuarioLogueado" class="badge bg-primary ms-2">
            {{ rolUsuarioLogueado }}
          </span>
        </span>

        <button class="btn btn-outline-light btn-sm" @click="cerrarSesion">
          Cerrar sesion
        </button>
      </div>
    </nav>

    <div class="row">
      <div class="col-md-2 bg-dark sidebar">
        <ul class="nav flex-column mt-4">
          <li class="nav-item">
            <a class="nav-link active text-white" href="#">Dashboard</a>
          </li>

          <li class="nav-item">
            <a class="nav-link text-white" href="#">Usuarios</a>
          </li>

          <li class="nav-item">
            <router-link class="nav-link text-white" to="/ordenes-compra">
              Ordenes de Compra
            </router-link>
          </li>

          <li class="nav-item">
            <router-link class="nav-link text-white" to="/hojas-vida-proveedores">
              Hojas de Vida Proveedores
            </router-link>
          </li>

          <li class="nav-item">
            <a class="nav-link text-white" href="#">Reportes</a>
          </li>
        </ul>
      </div>

      <div class="col-md-10 p-4">
        <h2 class="mb-4">Gestion de Usuarios</h2>

        <div class="row mb-4">
          <div class="col-md-4">
            <div class="card bg-primary text-white shadow">
              <div class="card-body">
                <h5>Total Usuarios</h5>
                <h1>{{ usuarios.length }}</h1>
              </div>
            </div>
          </div>

          <div class="col-md-4">
            <div class="card bg-success text-white shadow">
              <div class="card-body">
                <h5>Reportes</h5>
                <h1>35</h1>
              </div>
            </div>
          </div>

          <div class="col-md-4">
            <div class="card bg-dark text-white shadow">
              <div class="card-body">
                <h5>Instituciones</h5>
                <h1>18</h1>
              </div>
            </div>
          </div>
        </div>

        <div v-if="esAdmin" class="card shadow mb-4">
          <div class="card-header bg-dark text-white">
            {{ modoEditar ? "Editar Usuario" : "Registrar Usuario" }}
          </div>

          <div class="card-body">
            <form @submit.prevent="guardarUsuario">
              <div v-if="mensaje" class="alert alert-success">
                {{ mensaje }}
              </div>

              <div v-if="error" class="alert alert-danger">
                {{ error }}
              </div>

              <div class="row">
                <div class="col-md-3 mb-3">
                  <label class="form-label">Nombre</label>
                  <input
                    type="text"
                    class="form-control"
                    v-model="usuario.nombre"
                    minlength="3"
                    required
                  />
                </div>

                <div class="col-md-3 mb-3">
                  <label class="form-label">Correo</label>
                  <input
                    type="email"
                    class="form-control"
                    v-model="usuario.correo"
                    required
                  />
                </div>

                <div class="col-md-2 mb-3">
                  <label class="form-label">Contrasena</label>
                  <input
                    type="password"
                    class="form-control"
                    v-model="usuario.password"
                    minlength="4"
                    required
                  />
                </div>

                <div class="col-md-2 mb-3">
                  <label class="form-label">Rol</label>
                  <select class="form-select" v-model="usuario.rol" required>
                    <option value="USUARIO">USUARIO</option>
                    <option value="ADMIN">ADMIN</option>
                  </select>
                </div>

                <div class="col-md-2 mb-3 d-flex align-items-end">
                  <button type="submit" class="btn btn-primary w-100">
                    {{ modoEditar ? "Actualizar" : "Guardar" }}
                  </button>
                </div>
              </div>

              <button
                v-if="modoEditar"
                type="button"
                class="btn btn-secondary"
                @click="cancelarEdicion"
              >
                Cancelar
              </button>
            </form>
          </div>
        </div>

        <div class="card shadow">
          <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
            <span>Lista de Usuarios</span>
            <span class="small">{{ usuariosFiltrados.length }} encontrados</span>
          </div>

          <div class="card-body">
            <div class="row mb-3">
              <div class="col-md-6">
                <input
                  type="text"
                  class="form-control"
                  placeholder="Buscar por nombre, correo o rol"
                  v-model="busqueda"
                />
              </div>
            </div>

            <div v-if="cargando" class="alert alert-info">
              Cargando usuarios...
            </div>

            <table v-if="!cargando" class="table table-bordered table-hover">
              <thead class="table-dark">
                <tr>
                  <th>ID</th>
                  <th>Nombre</th>
                  <th>Correo</th>
                  <th>Rol</th>
                  <th v-if="esAdmin" class="text-center">Acciones</th>
                </tr>
              </thead>

              <tbody>
                <tr v-for="usuario in usuariosFiltrados" :key="usuario.id">
                  <td>{{ usuario.id }}</td>
                  <td>{{ usuario.nombre }}</td>
                  <td>{{ usuario.correo }}</td>
                  <td>
                    <span
                      class="badge"
                      :class="usuario.rol === 'ADMIN' ? 'bg-primary' : 'bg-secondary'"
                    >
                      {{ usuario.rol || "USUARIO" }}
                    </span>
                  </td>
                  <td v-if="esAdmin" class="text-center">
                    <button
                      class="btn btn-warning btn-sm me-2"
                      @click="editarUsuario(usuario)"
                    >
                      Editar
                    </button>

                    <button
                      class="btn btn-danger btn-sm"
                      @click="eliminarUsuario(usuario.id)"
                    >
                      Eliminar
                    </button>
                  </td>
                </tr>

                <tr v-if="usuariosFiltrados.length === 0">
                  <td :colspan="esAdmin ? 5 : 4" class="text-center">
                    No hay usuarios para mostrar.
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import UsuarioService from "@/services/UsuarioService";
import AuthService from "@/services/AuthService";

export default {
  name: "DashboardView",

  data() {
    return {
      usuarios: [],
      usuario: {
        id: null,
        nombre: "",
        correo: "",
        password: "",
        rol: "USUARIO",
      },
      usuarioLogueado: null,
      modoEditar: false,
      cargando: false,
      error: "",
      mensaje: "",
      busqueda: "",
    };
  },

  computed: {
    usuariosFiltrados() {
      const textoBusqueda = this.busqueda.trim().toLowerCase();

      if (!textoBusqueda) {
        return this.usuarios;
      }

      return this.usuarios.filter((usuario) => {
        const nombre = usuario.nombre ? usuario.nombre.toLowerCase() : "";
        const correo = usuario.correo ? usuario.correo.toLowerCase() : "";
        const rol = usuario.rol ? usuario.rol.toLowerCase() : "usuario";

        return (
          nombre.includes(textoBusqueda) ||
          correo.includes(textoBusqueda) ||
          rol.includes(textoBusqueda)
        );
      });
    },

    rolUsuarioLogueado() {
      return this.usuarioLogueado && this.usuarioLogueado.rol
        ? this.usuarioLogueado.rol
        : "USUARIO";
    },

    esAdmin() {
      return this.rolUsuarioLogueado === "ADMIN";
    },
  },

  mounted() {
    this.usuarioLogueado = AuthService.obtenerUsuario();
    this.listarUsuarios();
  },

  methods: {
    listarUsuarios() {
      this.cargando = true;

      UsuarioService.obtenerUsuarios()
        .then((response) => {
          this.usuarios = response.data;
        })
        .catch(() => {
          this.error = "No se pudieron cargar los usuarios.";
        })
        .finally(() => {
          this.cargando = false;
        });
    },

    guardarUsuario() {
      this.error = "";
      this.mensaje = "";

      if (!this.esAdmin) {
        this.error = "No tienes permisos para administrar usuarios.";
        return;
      }

      if (this.correoYaExiste()) {
        this.error = "Ya existe un usuario registrado con ese correo.";
        return;
      }

      if (this.modoEditar) {
        UsuarioService.actualizarUsuario(this.usuario.id, this.usuario)
          .then(() => {
            this.mensaje = "Usuario actualizado correctamente.";
            this.listarUsuarios();
            this.limpiarFormulario();
          })
          .catch(() => {
            this.error = "No se pudo actualizar el usuario.";
          });

        return;
      }

      UsuarioService.crearUsuario(this.usuario)
        .then(() => {
          this.mensaje = "Usuario registrado correctamente.";
          this.listarUsuarios();
          this.limpiarFormulario();
        })
        .catch(() => {
          this.error = "No se pudo registrar el usuario.";
        });
    },

    editarUsuario(usuarioSeleccionado) {
      this.usuario = {
        id: usuarioSeleccionado.id,
        nombre: usuarioSeleccionado.nombre,
        correo: usuarioSeleccionado.correo,
        password: usuarioSeleccionado.password || "",
        rol: usuarioSeleccionado.rol || "USUARIO",
      };

      this.modoEditar = true;
      this.error = "";
      this.mensaje = "";
    },

    eliminarUsuario(id) {
      if (!this.esAdmin) {
        this.error = "No tienes permisos para eliminar usuarios.";
        return;
      }

      const confirmar = confirm("Seguro que deseas eliminar este usuario?");

      if (!confirmar) {
        return;
      }

      this.error = "";
      this.mensaje = "";

      UsuarioService.eliminarUsuario(id)
        .then(() => {
          this.mensaje = "Usuario eliminado correctamente.";
          this.listarUsuarios();
        })
        .catch(() => {
          this.error = "No se pudo eliminar el usuario.";
        });
    },

    cancelarEdicion() {
      this.limpiarFormulario();
    },

    limpiarFormulario() {
      this.usuario = {
        id: null,
        nombre: "",
        correo: "",
        password: "",
        rol: "USUARIO",
      };

      this.modoEditar = false;
    },

    correoYaExiste() {
      const correoActual = this.usuario.correo.trim().toLowerCase();

      return this.usuarios.some((usuarioRegistrado) => {
        const mismoUsuario = usuarioRegistrado.id === this.usuario.id;
        const mismoCorreo =
          usuarioRegistrado.correo &&
          usuarioRegistrado.correo.toLowerCase() === correoActual;

        return mismoCorreo && !mismoUsuario;
      });
    },

    cerrarSesion() {
      AuthService.logout();
      this.$router.push("/login");
    },
  },
};
</script>

<style>
body {
  background: #f4f6f9;
}

.sidebar {
  min-height: 100vh;
}

.nav-link:hover {
  background: #495057;
  border-radius: 5px;
}

.card {
  border: none;
  border-radius: 15px;
}

.table {
  margin-bottom: 0;
}
</style>
