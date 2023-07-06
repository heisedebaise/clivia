<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { store } from '../store';
import { message } from './locale';
import { focus } from './cursor';
import { compositionStart, compositionEnd } from './composition';
import { keydown } from './keydown';
import { keyup } from './keyup';

const props = defineProps({
    id: String
});

const lines = ref([]);

const style = () => {
    let node = store.nodes[props.id];

    return node && node.text.length > 0 ? 'text' : 'placeholder';
};

const text = () => {
    let node = store.nodes[props.id];
    if (!node)
        return message('placeholder');

    if (props.id === store.focus && !node.text)
        return '';

    return node.text || message('placeholder');
};

const hasChild = () => {
    let node = store.nodes[props.id];

    return node && node.children && node.children.length > 0;
};

const branch = () => {
    let lines = [];

    let height = document.querySelector('#' + props.id).parentNode.offsetHeight;
    lines.push({ x1: 0, y1: height / 2, x2: 16, y2: height / 2 });

    lines.values = lines;
};

onMounted(() => {
    nextTick(branch);
});
</script>

<template>
    <div class="node">
        <div :id="id" :class="style()" :contenteditable="id === store.focus" @click="focus(id)"
            @compositionstart="compositionStart" @compositionend="compositionEnd" @keydown="keydown" @keyup="keyup">{{
                text() }}</div>
        <div v-if="hasChild()" class="children">
            <svg xmlns="http://www.w3.org/2000/svg" version="1.1" width="64px" height="4px">
                <line v-for="line in lines" :x1="line.x1" :y1="line.y1" :x2="line.x2" :y2="line.y2"
                    style="stroke:rgb(255,0,0);stroke-width:1" />
            </svg>
            <div>
                <div v-for="child in store.nodes[id].children" class="child">
                    <Node :id="child" />
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.node {
    display: flex;
    align-items: center;
}

.text,
.placeholder {
    border: 1px solid var(--border);
    border-radius: 4px;
    background-color: var(--background);
    padding: 0 8px;
    line-height: 2rem;
    outline: none;
    margin: 4px;
    white-space: nowrap;
}

.text {
    color: var(--color);
}

.placeholder {
    color: var(--placeholder);
}

.children {
    display: flex;
    align-items: stretch;
}
</style>