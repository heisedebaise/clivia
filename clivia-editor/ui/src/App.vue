<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { post, service } from './http';
import { now } from './components/time';
import { message } from './components/locale';
import Toolbar from './components/Toolbar.vue';
import Workspace from './components/Workspace.vue';

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
    workspace.value.annotation();
  });

  param.value.interval = setInterval(() => {
    let id = '';
    let array = [];
    let time = now() - 5000;
    for (let line of lines.value) {
      id += ',' + line.id;
      if (line.time && line.time > time)
        array.push(line);
    }
    id = id.substring(1);
    if (array.length === 0 && id === param.value.id)
      return;

    param.value.id = id;
    param.value.lines = JSON.stringify(array);
    service('/editor/put', param.value, data => {
      param.value.sync = data.sync;
    });
  }, 2000);
});

onUnmounted(() => {
  clearInterval(param.value.interval);
});
</script>

<template>
  <Workspace ref="workspace" :editable="true" :lines="lines" />
  <Toolbar :lines="lines" :workspace="workspace" @icon="toolbar" />
</template>

<style>
@import '@/assets/theme.css';
</style>
