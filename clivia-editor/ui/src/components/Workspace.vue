<script setup>
import { ref, provide, onMounted, onUnmounted } from 'vue';
import { store } from '../store';
import { service } from '../http';
import { now } from './time';
import { listen, trigger } from './event';
import { historyPut } from './history';
import { setCursor } from './cursor';
import { findByXy, findLine } from './line';
import Operate from './Operate.vue';
import Line from './Line.vue';
import Annotation from './Annotation.vue';
import Search from './Search.vue';
import Ai from './Ai.vue';

const props = defineProps({
    param: Object
});

const workspace = ref(null);
provide('workspace', workspace);
const param = {
    sync: 0,
};
const timer = {
    interval: 0,
    running: false,
    time: 0,
};

const move = (event) => {
    trigger('move', event);
};

const drop = (event) => {
    trigger('drop', event);
};

const click = (event) => {
    let id = findByXy(workspace.value, event.x, event.y);
    if (id === null)
        return;

    if (store.selects.length === 0)
        store.selects = [id, id];
    else
        store.selects[1] = id;
    let range = document.createRange();
    range.setStartBefore(document.querySelector('#' + store.selects[0]).children[0]);
    let end = document.querySelector('#' + store.selects[1]).children;
    range.setEndAfter(end[end.length - 1]);
    let selection = getSelection();
    selection.removeAllRanges();
    selection.addRange(range);
};

const focus = () => {
    store.selects = [];
};

const scroll = () => {
    trigger('scroll');
};

onMounted(() => {
    if (store.lines.length === 0)
        return;

    param.listener = props.param.listener;
    param.key = props.param.key || '';
    store.focus = store.lines[0].id;
    setCursor(store.focus, [0, 0, 0, 0]);
    trigger('annotation');

    for (let line of store.lines)
        if (line.time > timer.time)
            timer.time = line.time;
    historyPut();
    timer.interval = setInterval(() => window.put(false), 1000);
    listen('focus', focus);
});

window.put = (always) => {
    if (timer.running && !always && timer.time < now() - 5000)
        return;

    timer.running = true;
    let id = [];
    let array = [];
    let time = 0;
    for (let line of store.lines) {
        id.push(line.id);
        if (line.time && line.time > timer.time) {
            array.push(line);
            if (line.time > time)
                time = line.time;
        }
    }
    if (!always && array.length === 0) {
        timer.running = false;

        return;
    }

    historyPut();
    param.id = id.join(',');
    param.lines = JSON.stringify(array);
    service('/editor/put', param, data => {
        timer.running = false;
        param.sync = data.sync;
        timer.time = time;
    });
};

onUnmounted(() => {
    if (timer.interval > 0)
        clearInterval(timer.interval);
});
</script>

<template>
    <div ref="workspace" class="workspace" @mousemove="move" @touchmove="move" @mouseup="drop" @touchend="drop"
        @click.self="click" @scroll="scroll">
        <Line />
        <Annotation />
        <Operate />
        <Search />
        <Ai />
    </div>
</template>

<style scoped>
@import '@/assets/workspace.css';

.workspace {
    width: 100vw;
    height: 100vh;
    background-color: var(--background);
    overflow: auto;
}
</style>