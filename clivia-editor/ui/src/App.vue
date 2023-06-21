<script setup>
import { onMounted } from 'vue';
import { store } from './store';
import { newText } from './components/tag';
import Workspace from './components/Workspace.vue';

onMounted(() => {
  for (let i = 0; i < 1; i++)
    store.lines.push(newText('p'));
  store.focus = store.lines[0].id;
});
</script>

<template>
  <Workspace />
</template>

<style scoped></style>
