<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { store } from './store';
import { post, service } from './http';
import { now } from './components/time';
import { newText } from './components/tag';
import { historyPut } from './components/history';
import Toolbar from './components/Toolbar.vue';
import Workspace from './components/Workspace.vue';
import Readonly from './components/Readonly.vue';

window.mobile = /(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i.test(navigator.userAgent);

const mode = ref(0);
const param = {
  sync: 0,
  id: '',
};
const timer = {
  interval: 0,
  running: false,
  time: 0,
};

onMounted(() => {
  if (!location.search || location.search.length === 0)
    return;

  for (let search of location.search.substring(1).split('&')) {
    let nv = search.split('=');
    if (nv.length === 2)
      param[nv[0]] = nv[1];
  }
  post('/editor/get', param, json => {
    timer.time = now();
    param.sync = json.timestamp;
    let id = '';
    if (!json.data || json.data.length === 0) {
      json.data = [newText()];
    }
    for (let line of json.data) {
      id += ',' + line.id;
    }
    param.id = id.substring(1);
    store.lines = json.data;
    historyPut(store.lines);
    mode.value = param.readonly === 'true' ? 2 : 1;
  });

  timer.interval = setInterval(() => window.put(false), 1000);
});

window.put = (always) => {
  if (timer.running && !always && timer.time < now() - 5000)
    return;

  timer.running = true;
  let id = '';
  let array = [];
  let time = 0;
  for (let line of store.lines) {
    id += ',' + line.id;
    if (line.time && line.time > timer.time) {
      array.push(line);
      if (line.time > time)
        time = line.time;
    }
  }
  id = id.substring(1);
  if (!always && array.length === 0) {
    timer.running = false;

    return;
  }

  historyPut(store.lines);
  param.id = id;
  param.lines = JSON.stringify(array);
  service('/editor/put', param, data => {
    timer.running = false;
    param.sync = data.sync;
    timer.time = time;
  });
};

onUnmounted(() => {
  clearInterval(timer.interval);
});
</script>

<template>
  <Workspace v-if="mode === 1" />
  <Toolbar v-if="mode === 1" />
  <Readonly v-if="mode === 2" />
</template>

<style>
@import '@/assets/theme.css';
@import '@/assets/style.css';
</style>
