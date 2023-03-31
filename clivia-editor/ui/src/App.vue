<script setup>
import { ref, onMounted } from 'vue';
import Toolbar from './components/Toolbar.vue';
import Workspace from './components/Workspace.vue';

const workspace = ref(null);

const toolbar = (action) => {
  workspace.value.toolbar(action);
};

const lines = ref([]);

onMounted(() => {
  lines.value = [{
    id: '1',
    tag: 'p',
    texts: [{
      text: 'hello'
    }]
  }];
});
</script>

<template>
  <Toolbar @icon="toolbar" />
  <Workspace ref="workspace" :editable="true" :lines="lines" />
</template>

<style>
@import '@/assets/theme.css';
</style>
