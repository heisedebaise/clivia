<script setup>
import { ref, watch, onMounted, nextTick } from 'vue';
import { store } from '../store';
import { message } from './locale';
import { focus } from './cursor';
import { compositionStart, compositionEnd } from './composition';
import { keydown } from './keydown';
import { keyup } from './keyup';
import { listen, trigger } from './event';
import Icon from './Icon.vue';

const props = defineProps({
    id: String
});

const branch = ref({
    width: 64,
    height: 0,
    lines: [],
});

const index = () => {
    let node = store.nodes[props.id];

    return node ? (node.index || '') : '';
};

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
    if (event.id != props.id)
        return;

    branch.value.height = 0;
    nextTick(() => {
        let node = store.nodes[props.id];
        if (!node || !node.children || node.children.length === 0)
            return;

        let parent = document.querySelector('#' + props.id).parentNode;
        let height = parent.offsetHeight;
        branch.value.height = height;

        let ys = [];
        let top = parent.offsetTop;
        for (let id of node.children) {
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
};

const operate = (event) => {
    focus(props.id);
    trigger('operate', event);
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
        <div :id="id" class="content" @click="focus(id)">
            <div class="index">{{ index() }}</div>
            <div :class="style()" :contenteditable="id === store.focus" @compositionstart="compositionStart"
                @compositionend="compositionend" @keydown="keydown" @keyup="keyup">{{ text() }}</div>
        </div>
        <div class="operate" @click="operate">
            <Icon name="more" :enable="false" />
        </div>
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

.content {
    display: flex;
    align-items: stretch;
    border: 1px solid var(--border);
    border-radius: 4px;
    background-color: var(--background);
    margin: 4px 4px 4px 0;
}

.index,
.text,
.placeholder {
    line-height: 2rem;
    padding: 0 8px;
    white-space: nowrap;
    outline: none;
}

.index {
    border-right: 1px solid var(--border);
    cursor: default;
}

.text {
    color: var(--color);
}

.index,
.placeholder {
    color: var(--placeholder);
}

.children {
    display: flex;
    align-items: stretch;
}

.branch {
    stroke: var(--branch);
    stroke-width: 1px;
}

.operate:hover {
    background-color: var(--hover-bg);
    border-radius: 4px;
}
</style>