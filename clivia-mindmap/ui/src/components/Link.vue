<script setup>
import { ref, onMounted } from 'vue';
import { store } from '../store';
import { listen } from './event';

const lines = ref([]);
const min = 64;
const arrow = 10;
const arrowD = `M 0 0 L ${arrow} ${arrow / 2} L 0 ${arrow} z`;
const colors = ['gray', 'brown', 'orange', 'yellow', 'green', 'blue', 'purple', 'pink', 'red'];

const draw = () => {
    let list = [];
    for (let id in store.nodes) {
        let node = store.nodes[id];
        if (!node.link)
            continue;

        let nodeRectangle = rectangle(node.id);
        if (!nodeRectangle)
            continue;

        let linkRectangle = rectangle(node.link);
        if (!linkRectangle)
            continue;

        let line = {};
        let width = Math.abs(nodeRectangle.left - linkRectangle.left);
        let height = Math.abs(nodeRectangle.top - linkRectangle.top);
        if (width < min) {
            line.width = min + width;
            line.height = Math.abs(nodeRectangle.top - linkRectangle.top) + arrow / 2;
            line.left = Math.max(nodeRectangle.left, linkRectangle.left) - line.width;
            let start = nodeRectangle.left > linkRectangle.left ? line.width : (line.width - width);
            let end = nodeRectangle.left > linkRectangle.left ? (line.width - width) : line.width;
            if (nodeRectangle.top > linkRectangle.top) {
                line.top = linkRectangle.y - arrow / 2;
                line.path = `M ${start} ${line.height} C 0 ${line.height}, 0 ${arrow / 2}, ${end - arrow} ${arrow / 2}`;
            } else {
                line.top = nodeRectangle.y;
                line.path = `M ${start} 0 C 0 0, 0 ${line.height - arrow / 2}, ${end - arrow} ${line.height - arrow / 2}`;
            }
        } else if (height < min) {
            line.width = Math.abs(nodeRectangle.x - linkRectangle.x) + arrow / 2;
            line.height = min + height;
            line.top = Math.max(nodeRectangle.top, linkRectangle.top) - line.height;
            let start = nodeRectangle.top > linkRectangle.top ? line.height : (line.height - height);
            let end = nodeRectangle.top > linkRectangle.top ? (line.height - height) : line.height;
            if (nodeRectangle.x > linkRectangle.x) {
                line.left = linkRectangle.x - arrow / 2;
                line.path = `M ${line.width} ${start} C ${line.width} 0, ${arrow / 2} 0, ${arrow / 2} ${end - arrow}`;
            } else {
                line.left = nodeRectangle.x;
                line.path = `M 0 ${start} C 0 0, ${line.width - arrow / 2} 0, ${line.width - arrow / 2} ${end - arrow}`;
            }
        } else if (nodeRectangle.x < linkRectangle.left) {
            line.width = linkRectangle.left - nodeRectangle.x;
            line.left = nodeRectangle.x;
            if (nodeRectangle.y > linkRectangle.y) {
                line.height = nodeRectangle.y - linkRectangle.y + arrow / 2;
                line.top = linkRectangle.y - arrow / 2;
                line.path = `M 0 ${line.height} C 0 ${arrow / 2}, 0 ${arrow / 2}, ${line.width - arrow} ${arrow / 2}`;
            } else {
                line.height = linkRectangle.y - nodeRectangle.bottom + arrow / 2;
                line.top = nodeRectangle.bottom;
                line.path = `M 0 0 C 0 ${line.height - arrow / 2}, 0 ${line.height - arrow / 2}, ${line.width - arrow} ${line.height - arrow / 2}`;
            }
        } else if (nodeRectangle.left > linkRectangle.x) {
            line.width = nodeRectangle.left - linkRectangle.x + arrow / 2;
            line.left = linkRectangle.x - arrow / 2;
            if (nodeRectangle.y > linkRectangle.y) {
                line.height = nodeRectangle.y - linkRectangle.bottom;
                line.top = linkRectangle.bottom;
                line.path = `M ${line.width} ${line.height} C ${arrow / 2} ${line.height}, ${arrow / 2} ${line.height}, ${arrow / 2} ${arrow}`;
            } else {
                line.height = linkRectangle.top - nodeRectangle.y;
                line.top = nodeRectangle.y;
                line.path = `M ${line.width} 0 C ${arrow / 2} 0, ${arrow / 2} 0, ${arrow / 2} ${line.height - arrow}`;
            }
        }
        if (line.path) {
            line.color = colors[list.length % colors.length];
            list.push(line);
        }
    }
    lines.value = list;
};

const rectangle = (id) => {
    let node = document.querySelector('#' + id);
    if (!node)
        return null;

    return {
        left: node.offsetLeft,
        top: node.offsetTop,
        right: node.offsetLeft + node.offsetWidth,
        bottom: node.offsetTop + node.offsetHeight,
        x: node.offsetLeft + node.offsetWidth / 2,
        y: node.offsetTop + node.offsetHeight / 2,
    };
};

onMounted(() => {
    listen('link', draw);
    draw();
});
</script>

<template>
    <div v-for="(line, index) in lines" class="link" :style="{ left: line.left + 'px', top: line.top + 'px' }">
        <svg :width="line.width + 'px'" :height="line.height + 'px'">
            <defs>
                <marker :id="'arrow' + index" markerUnits="strokeWidth" :markerWidth="arrow" :markerHeight="arrow" refX="0"
                    :refY="arrow / 2" orient="auto">
                    <path :d="arrowD" :fill="line.color" />
                </marker>
            </defs>
            <path class="line" :d="line.path" :stroke="line.color" :marker-end="'url(#arrow' + index + ')'" />
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
    stroke-width: 1px;
    stroke-dasharray: 5, 5;
}
</style>