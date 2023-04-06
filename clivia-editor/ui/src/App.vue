<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import Toolbar from './components/Toolbar.vue';
import Workspace from './components/Workspace.vue';

const workspace = ref(null);

const toolbar = (action) => {
  workspace.value.toolbar(action);
};

const lines = ref([]);
const timer = ref(0);

onMounted(() => {
  lines.value = [{
    id: 'id1',
    tag: 'p',
    className: 'empty',
    placeholder: '123456',
    texts: [{
      text: ''
    }]
  }];
  timer.value = setInterval(() => {
    workspace.value.second();
  }, 1000);
});

onUnmounted(() => {
  clearInterval(timer.value);
});
</script>

<template>
  <Toolbar :lines="lines" @icon="toolbar" />
  <Workspace ref="workspace" :editable="true" :lines="lines" />
</template>

<style>
@import '@/assets/theme.css';
</style>
