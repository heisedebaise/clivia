<script setup>
import { ref, onMounted } from 'vue';
import { service } from './http';
import { newText } from './components/tag';
import Workspace from './components/Workspace.vue';
import { store } from './store';

window.safari = /^((?!chrome|android).)*safari/i.test(navigator.userAgent);
const mode = ref(0);
const param = {};

onMounted(() => {
  if (!location.search || location.search.length === 0)
    return;

  for (let search of location.search.substring(1).split('&')) {
    let nv = search.split('=');
    if (nv.length === 2)
      param[nv[0]] = nv[1];
  }
  if (!param.key)
    param.key = '';
  service('/editor/get', param, data => {
    if (!data || data.length === 0) {
      data = [newText()];
    }
    for (let line of data)
      if (line.tag === 'text')
        line.tag = 'p';
    store.lines = data;
    mode.value = param.readonly === 'true' ? 2 : 1;
  });
});
</script>

<template>
  <Workspace v-if="mode === 1" :param="param" />
</template>

<style scoped></style>
