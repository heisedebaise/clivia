<script setup>
import { ref } from 'vue';
import Toolbar from './components/Toolbar.vue';
import Workspace from './components/Workspace.vue';

const workspace = ref(null);

const toolbar = (action) => {
  workspace.value.toolbar(action);
};
</script>

<template>
  <Toolbar @icon="toolbar" />
  <Workspace ref="workspace" />
</template>

<style>
@import '@/assets/theme.css';
</style>
