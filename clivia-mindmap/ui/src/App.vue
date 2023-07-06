<script setup>
import { ref, onMounted } from 'vue';
import { store } from './store';
import { newNode } from './components/node';

const main = ref({
  id: ''
});

onMounted(() => {
  let node = newNode('');
  node.main = true;
  store.nodes[node.id] = node;
  main.value.id = node.id;
});
</script>

<template>
  <div v-if="main.id" class="mindmap-table">
    <div class="mindmap-cell">
      <Node :id="main.id" />
    </div>
  </div>
</template>

<style scoped>
.mindmap-table {
  display: table;
  min-height: 100%;
  margin: auto;
}

.mindmap-cell {
  display: table-cell;
  vertical-align: middle;
}
</style>
