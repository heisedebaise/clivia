<script setup>
import { ref } from 'vue';

const text = ref('');

window.debug = (value) => text.value = value;
</script>

<template>
    <div class="debug">{{ text }}</div>
</template>

<style scoped>
.debug {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: var(--background);
    border-top: 1px solid var(--border);
    padding: 2vw;
}
</style>