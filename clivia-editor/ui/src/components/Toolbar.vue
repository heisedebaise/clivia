<script setup>
import { ref, onMounted } from 'vue';
import { setTag } from './keydown';
import { bindSelect } from './cursor';
import { bold, italic, underline, linethrough } from './style';
import { newImage, newDivider } from './tag';
import Icon from './Icon.vue';
import Tag from './Tag.vue';
import Annotation from './Annotation.vue';

defineProps({
    lines: Array,
    workspace: Object
});

defineEmits(['icon']);

const enable = ref({
    undo: false,
    redo: false,
    header: true,
    bold: false,
    italic: false,
    underline: false,
    linethrough: false,
    annotation: false,
    divider: true,
    quote: false,
    link: false,
    backlog: false,
    image: true,
    direction: true,
});

const tag = ref(null);

const header = (e) => {
    let offset = findOffset(e);
    tag.value.show(offset.left, offset.top);
    setTag(tag.value);
};

const annotation = ref(null);

const showAnnotation = (e) => {
    let offset = findOffset(e);
    annotation.value.show(offset.left, offset.top);
};

const findOffset = (e) => {
    let left = -1;
    let top = -1;
    let node = e.target;
    for (let i = 0; i < 1024; i++) {
        if (node.className && typeof node.className === 'string') {
            if (node.className.indexOf('icon') > -1) {
                left = node.offsetLeft;
            } else if (node.className === 'toolbar') {
                top = node.offsetHeight - 1;

                break;
            }
        }

        node = node.parentNode;
    }

    return { left, top };
};

onMounted(() => {
    bindSelect((select) => {
        enable.value.bold = select;
        enable.value.italic = select;
        enable.value.underline = select;
        enable.value.linethrough = select;
        enable.value.annotation = select;
    });
});
</script>

<template>
    <div class="toolbar">
        <div></div>
        <Icon name="undo" :enable="enable.undo" @click="$emit('icon', 'undo')" />
        <Icon name="redo" :enable="enable.redo" @click="$emit('icon', 'redo')" />
        <Icon name="header" :enable="enable.header" @click="header" />
        <Icon name="bold" :enable="enable.bold" @click="bold(lines)" />
        <Icon name="italic" :enable="enable.italic" @click="italic(lines)" />
        <Icon name="underline" :enable="enable.underline" @click="underline(lines)" />
        <Icon name="linethrough" :enable="enable.linethrough" @click="linethrough(lines)" />
        <Icon name="annotation" :enable="enable.annotation" @click="showAnnotation" />
        <Icon name="divider" :enable="enable.divider" @click="newDivider(lines)" />
        <Icon name="quote" :enable="enable.quote" @click="$emit('icon', 'quote')" />
        <Icon name="link" :enable="enable.link" @click="$emit('icon', 'link')" />
        <Icon name="backlog" :enable="enable.backlog" @click="$emit('icon', 'backlog')" />
        <Icon name="image" :enable="enable.image" @click="newImage(lines)" />
        <Icon name="direction" :enable="enable.direction" @click="$emit('icon', 'direction')" />
        <div></div>
    </div>
    <Tag ref="tag" :names="['h1', 'h2', 'h3']" :lines="lines" />
    <Annotation ref="annotation" :lines="lines" :workspace="workspace" />
</template>

<style>
.toolbar {
    display: flex;
    align-items: center;
    justify-content: space-around;
    border-top: 1px solid var(--border);
    border-bottom: 1px solid var(--border);
    height: 40px;
}

.toolbar .icon-enable:hover {
    background-color: var(--icon-hover-bg);
    border-radius: 4px;
    cursor: pointer;
}

.toolbar .icon-enable:hover path {
    fill: var(--icon-hover);
}

.toolbar .icon-enable:hover ellipse {
    stroke: var(--icon-hover);
}
</style>