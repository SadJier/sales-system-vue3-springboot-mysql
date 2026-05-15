import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import axios from './axios/index'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)
app.config.globalProperties.$http = axios;

app.use(ElementPlus)
app.use(createPinia())
app.use(router)

app.mount('#app')
