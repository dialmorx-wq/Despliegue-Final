import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// BOOTSTRAP CSS
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-icons/font/bootstrap-icons.css'

// BOOTSTRAP JS
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

createApp(App)
.use(router)
.mount('#app')
