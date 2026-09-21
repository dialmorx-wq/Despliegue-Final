import axios from "axios";

const API_URL = "https://sgarpf-production.up.railway.app/api/auth";
const USUARIO_KEY = "usuarioLogueado";

class AuthService {
  login(credenciales) {
    return axios.post(`${API_URL}/login`, credenciales);
  }

  guardarUsuario(usuario) {
    localStorage.setItem(USUARIO_KEY, JSON.stringify(usuario));
  }

  obtenerUsuario() {
    const usuario = localStorage.getItem(USUARIO_KEY);
    return usuario ? JSON.parse(usuario) : null;
  }

  estaAutenticado() {
    return this.obtenerUsuario() !== null;
  }

  logout() {
    localStorage.removeItem(USUARIO_KEY);
  }
}

export default new AuthService();
