import { createApp } from 'vue'
import pinia from '@/stores'

import App from '@/App.vue'
import router from '@/router'
// 引入全局样式
import '@/assets/css/main.css'

const app = createApp(App)

app.use(pinia)
app.use(router)

app.mount('#app')
