import './assets/theme.css';
import './assets/main.css';

import { createApp } from 'vue';
import App from './App.vue';
import Node from './components/Node.vue';

const app = createApp(App);
app.component('Node', Node);
app.mount('#app');
