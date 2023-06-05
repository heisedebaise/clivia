<script setup>
import { ref } from 'vue';

const text = ref({
    index: 0,
    message: '',
});

window.debug = (message) => text.value = { index: text.value.index + 1, message };
</script>

<template>
    <div class="debug">{{ text.index }}::{{ text.message }}</div>
</template>

<style scoped>
.debug {
    position: fixed;
    left: 0;
    right: 0;
    top: 0;
    background-color: var(--background);
    border-bottom: 1px solid var(--border);
    padding: 2vw;
}
</style>