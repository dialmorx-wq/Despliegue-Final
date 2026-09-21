<template>
  <div class="login-page">
    <div class="login-panel shadow">
      <div class="login-header">
        <h1>SGA_RPF</h1>
        <p>Sistema de Gestion de Usuarios</p>
      </div>

      <form @submit.prevent="iniciarSesion">
        <div class="mb-3">
          <label class="form-label">Correo</label>
          <input
            type="email"
            class="form-control"
            v-model="credenciales.correo"
            autocomplete="email"
            required
          />
        </div>

        <div class="mb-3">
          <label class="form-label">Contrasena</label>
          <input
            type="password"
            class="form-control"
            v-model="credenciales.password"
            autocomplete="current-password"
            required
          />
        </div>

        <div v-if="error" class="alert alert-danger">
          {{ error }}
        </div>

        <button type="submit" class="btn btn-primary w-100" :disabled="cargando">
          {{ cargando ? "Ingresando..." : "Ingresar" }}
        </button>
      </form>
    </div>
  </div>
</template>

<script>
import AuthService from "@/services/AuthService";

export default {
  name: "LoginView",

  data() {
    return {
      credenciales: {
        correo: "",
        password: "",
      },
      cargando: false,
      error: "",
    };
  },

  methods: {
    iniciarSesion() {
      this.cargando = true;
      this.error = "";

      AuthService.login(this.credenciales)
        .then((response) => {
          AuthService.guardarUsuario(response.data);
          this.$router.push("/dashboard");
        })
        .catch(() => {
          this.error = "Correo o contrasena incorrectos.";
        })
        .finally(() => {
          this.cargando = false;
        });
    },
  },
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f4f6f9;
  padding: 24px;
}

.login-panel {
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  border-radius: 12px;
  padding: 32px;
}

.login-header {
  margin-bottom: 24px;
  text-align: center;
}

.login-header h1 {
  margin: 0;
  font-size: 32px;
  font-weight: 700;
}

.login-header p {
  margin: 8px 0 0;
  color: #6c757d;
}
</style>
