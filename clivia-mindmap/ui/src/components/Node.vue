<script setup>
import { ref } from 'vue';
import { store } from '../store';
import { message } from './locale';

const props = defineProps({
    id: String
});

const text = () => {
    let node = store.nodes[props.id];
    if (!node)
        return message('placeholder');

    return node.text || message('placeholder');
};

const focus = () => {
    store.focus = props.id;
};
</script>

<template>
    <div class="nodes">
        <div :id="id" class="node" :contenteditable="store.focus === id" @click="focus">{{ text() }}</div>
    </div>
</template>

<style scoped>
.nodes {
    display: flex;
    align-items: center;
}

.node {
    border: 1px solid var(--border);
    border-radius: 4px;
    padding: 4px;
}
</style>