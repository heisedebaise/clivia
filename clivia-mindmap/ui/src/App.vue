<script setup>
import { onMounted } from 'vue';
import { store } from './store';
import { newNode } from './components/node';
import Link from './components/Link.vue';
import Operate from './components/Operate.vue';

onMounted(() => {
  let node = newNode('');
  node.main = true;
  node.index = '0';
  store.nodes[node.id] = node;
  store.main = node.id;
});
</script>

<template>
  <Link />
  <div v-if="store.main" class="mindmap-table">
    <div class="mindmap-cell">
      <Node :id="store.main" />
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
