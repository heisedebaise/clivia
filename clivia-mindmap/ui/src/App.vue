<script setup>
import { onMounted } from 'vue';
import { store } from './store';
import Node from './components/Node.vue';

onMounted(() => {
  store.nodes = {
    idmain: {}
  };
});
</script>

<template>
  <Node id="idmain" />
</template>

<style scoped></style>
