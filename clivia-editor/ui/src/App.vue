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

const key = ref('');
const lines = ref([]);
const timer = ref({
  interval: 0,
  sync: 0,
});

onMounted(() => {
  if (!location.search)
    return;

  let indexOf = location.search.indexOf('.');
  if (indexOf === -1)
    key.value = location.search.substring(1);
  else
    key.value = location.search.substring(1, indexOf);
  post('/editor/get', { key: key.value }, json => {
    timer.value.sync = json.timestamp;
    for (let line of json.data)
      line.placeholder = message('placeholder.' + line.tag);
    lines.value = json.data;
    workspace.value.annotation();
  });

  timer.value.interval = setInterval(() => {
    let id = '';
    let array = [];
    let time = now() - 5000;
    for (let line of lines.value) {
      id += ',' + line.id;
      if (line.time && line.time > time)
        array.push(line);
    }
    if (array.length === 0)
      return;

    workspace.value.annotation();
    service('/editor/save', { key: key.value, id: id.substring(1), lines: JSON.stringify(array), sync: timer.value.sync }, data => {
      timer.value.sync = data.sync;
    });
  }, 1000);
});

onUnmounted(() => {
  clearInterval(timer.value.interval);
});
</script>

<template>
  <Workspace ref="workspace" :editable="true" :lines="lines" />
  <Toolbar :lines="lines" :workspace="workspace" @icon="toolbar" />
</template>

<style>
@import '@/assets/theme.css';
</style>
