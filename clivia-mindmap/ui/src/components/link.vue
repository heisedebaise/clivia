<script setup>
import { ref, onMounted } from 'vue';
import { store } from '../store';
import { listen } from './event';

const lines = ref([]);
const min = 64;

const draw = () => {
    let list = [];
    for (let id in store.nodes) {
        let node = store.nodes[id];
        if (!node.link)
            continue;

        let nodePosition = position(node.id);
        if (!nodePosition)
            continue;

        let linkPosition = position(node.link);
        if (!linkPosition)
            continue;

        let line = {
            width: Math.max(min, Math.abs(nodePosition.x - linkPosition.x)),
            height: Math.max(min, Math.abs(nodePosition.y - linkPosition.y)),
        };
        if (nodePosition.x < linkPosition.x && nodePosition.y === linkPosition.y) {
            line.path = `M 0 ${line.height} C 0 0, ${line.width} 0, ${line.width} ${line.height}`;
            line.style = `left:${Math.min(nodePosition.x, linkPosition.x)}px;top:${nodePosition.y - line.height}px`;
        } else if (nodePosition.x > linkPosition.x && nodePosition.y === linkPosition.y)
            line.path = `M ${line.width} 0 C ${line.width} ${line.height}, 0 ${line.height}, 0 0`;
        else if (line.width === min)
            line.path = `M 0 0 C ${line.width} 0, ${line.width} ${line.height}, 0 ${line.height}`;
        else if (nodePosition.x < linkPosition.x && nodePosition.y < linkPosition.y)
            line.path = `M 0 0 C ${line.width >> 1} 0, ${line.width} ${line.height >> 1}, ${line.width} ${line.height}`;
        else if (nodePosition.x < linkPosition.x && nodePosition.y > linkPosition.y)
            line.path = `M 0 ${line.height} C ${line.width >> 1} ${line.height}, ${line.width} ${line.height >> 1}, ${line.width} 0`;
        else if (nodePosition.x > linkPosition.x && nodePosition.y < linkPosition.y)
            line.path = `M ${line.width} 0 C ${line.width >> 1} 0, 0 ${line.height >> 1}, 0 ${line.height}`;
        else if (nodePosition.x > linkPosition.x && nodePosition.y > linkPosition.y)
            line.path = `M ${line.width} ${line.height} C ${line.width >> 1} ${line.height}, 0 ${line.height >> 1}, 0 0`;
        else
            line.path = `M 0 0 L ${line.width} ${line.height}`;
        if (!line.style)
            line.style = `left:${Math.min(nodePosition.x, linkPosition.x)}px;top:${Math.min(nodePosition.y, linkPosition.y)}px`;
        list.push(line);
    }
    lines.value = list;
};

const position = (id) => {
    let node = document.querySelector('#' + id);
    if (!node)
        return null;

    return { x: node.offsetLeft + node.offsetWidth / 2, y: node.offsetTop + node.offsetHeight / 2 };
};

onMounted(() => {
    listen('link', draw);
    draw();
});
</script>

<template>
    <div v-for="line in lines" class="link" :style="line.style">
        <svg xmlns="http://www.w3.org/2000/svg" version="1.1" :width="line.width + 'px'" :height="line.height + 'px'">
            <path class="line" :d="line.path" />
        </svg>
    </div>
</template>

<style scoped>
.link {
    position: absolute;
    z-index: -1;
}

.line {
    fill: none;
    stroke: var(--link);
    stroke-width: 1px;
    stroke-dasharray: 5, 5;
}
</style>