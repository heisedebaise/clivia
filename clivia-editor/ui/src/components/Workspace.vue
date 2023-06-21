<script setup>
import { ref, provide } from 'vue';
import { trigger } from './event';
import Operate from './Operate.vue';
import Line from './Line.vue';

const workspace = ref(null);
provide('workspace', workspace);
const operate = ref(null);
</script>

<template>
    <div ref="workspace" class="workspace" @mousemove="operate.move" @touchmove="operate.move" @mouseup="operate.done"
        @touchend="operate.done" @scroll="trigger('scroll')">
        <Line />
        <Operate ref="operate" />
    </div>
</template>

<style scoped>
.workspace {
    width: 100vw;
    height: 100vh;
    background-color: var(--background);
    overflow: auto;
}
</style>