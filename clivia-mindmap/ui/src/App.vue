<script setup>
import { ref, onMounted } from 'vue';
import { store } from './store';
import { newNode } from './components/node';
import Link from './components/link.vue';
import Operate from './components/Operate.vue';

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
  <Link />
  <div v-if="main.id" class="mindmap-table">
    <div class="mindmap-cell">
      <Node :id="main.id" />
    </div>
  </div>
  <Operate />
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
