<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { post, service } from './http';
import { now } from './components/time';
import { message } from './components/locale';
import Toolbar from './components/Toolbar.vue';
import Workspace from './components/Workspace.vue';
import Readonly from './components/Readonly.vue';

const mode = ref(0);
const workspace = ref(null);
const toolbar = (action) => {
  workspace.value.toolbar(action);
};

const param = ref({
  interval: 0,
  sync: 0,
  id: '',
});
const lines = ref([]);

onMounted(() => {
  if (!location.search || location.search.length === 0)
    return;

  for (let search of location.search.substring(1).split('&')) {
    let nv = search.split('=');
    if (nv.length === 2)
      param.value[nv[0]] = nv[1];
  }
  post('/editor/get', param.value, json => {
    param.value.sync = json.timestamp;
    let id = '';
    for (let line of json.data) {
      line.placeholder = message('placeholder.' + line.tag);
      id += ',' + line.id;
    }
    param.value.id = id.substring(1);
    lines.value = json.data;
    mode.value = param.value.readonly === 'true' ? 2 : 1;
    if (workspace.value)
      workspace.value.annotation();
  });

  param.value.interval = setInterval(() => window.put(false), 2000);
});

window.put = (always) => {
  let id = '';
  let array = [];
  let time = now() - 5000;
  for (let line of lines.value) {
    id += ',' + line.id;
    if (line.time && line.time > time)
      array.push(line);
  }
  id = id.substring(1);
  if (!always && array.length === 0 && id === param.value.id)
    return;

  param.value.id = id;
  param.value.lines = JSON.stringify(array);
  service('/editor/put', param.value, data => {
    param.value.sync = data.sync;
  });
};

onUnmounted(() => {
  clearInterval(param.value.interval);
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
