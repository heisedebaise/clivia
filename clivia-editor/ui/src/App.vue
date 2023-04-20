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
const save = ref({
  interval: 0,
  sync: 0,
  id: '',
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
    save.value.sync = json.timestamp;
    let id = '';
    for (let line of json.data) {
      line.placeholder = message('placeholder.' + line.tag);
      id += ',' + line.id;
    }
    save.value.id = id.substring(1);
    lines.value = json.data;
    workspace.value.annotation();
  });

  save.value.interval = setInterval(() => {
    let id = '';
    let array = [];
    let time = now() - 5000;
    for (let line of lines.value) {
      id += ',' + line.id;
      if (line.time && line.time > time)
        array.push(line);
    }
    if (array.length === 0 && id === save.value.id)
      return;

    if (id.length > 0)
      save.value.id = id.substring(1);
    service('/editor/save', { key: key.value, id: save.value.id, lines: JSON.stringify(array), sync: save.value.sync }, data => {
      save.value.sync = data.sync;
    });
  }, 2000);
});

onUnmounted(() => {
  clearInterval(save.value.interval);
});
</script>

<template>
  <Workspace ref="workspace" :editable="true" :lines="lines" />
  <Toolbar :lines="lines" :workspace="workspace" @icon="toolbar" />
</template>

<style>
@import '@/assets/theme.css';
</style>
