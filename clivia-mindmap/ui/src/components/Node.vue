<script setup>
import { ref, onMounted, nextTick, triggerRef } from 'vue';
import { store } from '../store';
import { message } from './locale';
import { focus } from './cursor';
import { compositionStart, compositionEnd } from './composition';
import { keydown } from './keydown';
import { keyup } from './keyup';
import { listen, trigger } from './event';
import { newNode, removeNode } from './node';

const props = defineProps({
    id: String
});

const branch = ref({
    width: 64,
    height: 0,
    lines: [],
});

const isMain = () => {
    let node = store.nodes[props.id];

    return node && node.main;
}

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

const draw = (event) => {
    if (event.id != props.id || !hasChild())
        return;

    if (event.type === 'remove')
        branch.value.height = 0;

    nextTick(() => {
        let node = document.querySelector('#' + props.id).parentNode;
        let height = node.offsetHeight;
        branch.value.height = height;

        let ys = [];
        let top = node.offsetTop;
        for (let id of store.nodes[props.id].children) {
            let child = document.querySelector('#' + id);
            if (child)
                ys.push(child.offsetTop + (child.offsetHeight >> 1) - top);
        }

        let lines = [];
        if (ys.length === 1)
            lines.push({ x1: 0, y1: height / 2, x2: branch.value.width, y2: height / 2 });
        else {
            let x = branch.value.width >> 1;
            lines.push({ x1: 0, y1: height / 2, x2: x, y2: height / 2 });
            lines.push({ x1: x, y1: ys[0], x2: x, y2: ys[ys.length - 1] });
            for (let y of ys)
                lines.push({ x1: x, y1: y, x2: branch.value.width, y2: y });
        }
        branch.value.lines = lines;
    });

    let node = store.nodes[props.id];
    if (!node.main && node.parent)
        trigger('branch', { type: event.type, id: node.parent });
};

const remove = () => removeNode(store.nodes[props.id]);

const plus = () => {
    let node = store.nodes[props.id];
    if (!node.children)
        node.children = [];
    let child = newNode(node.id);
    node.children.push(child.id);
    focus(child.id);
    draw({ type: 'new', id: node.id });
};

const compositionend = () => {
    compositionEnd();
    keyup();
}

onMounted(() => {
    draw({ id: props.id });
    listen('branch', draw);
});
</script>

<template>
    <div class="node">
        <div v-if="!isMain()" class="remove" @click="remove">-</div>
        <div :id="id" :class="style()" :contenteditable="id === store.focus" @click="focus(id)"
            @compositionstart="compositionStart" @compositionend="compositionend" @keydown="keydown" @keyup="keyup">{{
                text() }}</div>
        <div class="plus" @click="plus">+</div>
        <div v-if="hasChild()" class="children">
            <svg xmlns="http://www.w3.org/2000/svg" version="1.1" :width="branch.width + 'px'"
                :height="branch.height + 'px'">
                <line v-for="line in branch.lines" class="branch" :x1="line.x1" :y1="line.y1" :x2="line.x2" :y2="line.y2" />
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

.remove,
.plus {
    width: 1rem;
    line-height: 1rem;
    font-size: 1rem;
    border: 1px solid var(--border);
    border-radius: 16px;
    text-align: center;
    color: var(--border);
    cursor: pointer;
}

.plus:hover {
    border: 1px solid var(--color);
    color: var(--color);
}

.children {
    display: flex;
    align-items: stretch;
}

.branch {
    stroke: var(--branch);
    stroke-width: 0.5px;
}
</style>