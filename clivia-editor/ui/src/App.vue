<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { post, service } from './http';
import { now } from './components/time';
import { newText } from './components/tag';
import { historyPut } from './components/history';
import Toolbar from './components/Toolbar.vue';
import Workspace from './components/Workspace.vue';
import Readonly from './components/Readonly.vue';

window.safari = /^((?!chrome|android).)*safari/i.test(navigator.userAgent);

const mode = ref(0);
const workspace = ref(null);
const param = {
  sync: 0,
  id: '',
};
const timer = {
  interval: 0,
  running: false,
  time: 0,
};
const lines = ref([]);

const toolbar = (action, data) => {
  if (action === 'history') {
    if (data)
      lines.value = JSON.parse(data);
  } else
    workspace.value.toolbar(action);
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
    lines.value = json.data;
    historyPut(lines.value);
    mode.value = param.readonly === 'true' ? 2 : 1;
    if (workspace.value)
      workspace.value.annotation();
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
  for (let line of lines.value) {
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

  historyPut(lines.value);
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
  <Workspace v-if="mode === 1" ref="workspace" :lines="lines" />
  <Toolbar v-if="mode === 1" :lines="lines" @icon="toolbar" />
  <Readonly v-if="mode === 2" :lines="lines" />
</template>

<style>
@import '@/assets/theme.css';
@import '@/assets/style.css';
</style>
