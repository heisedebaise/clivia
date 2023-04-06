<script setup>
import { ref, onMounted } from 'vue';
import { bindSelect } from './cursor';
import { bold, italic, underline, linethrough } from './style';
import { newDivider } from './tag';
import Icon from './Icon.vue';

const props = defineProps({
    lines: Array,
    workspace: Object
});

const enable = ref({
    undo: false,
    redo: false,
    header: true,
    bold: false,
    italic: false,
    underline: false,
    linethrough: false,
    divider: true,
    quote: false,
    link: false,
    backlog: false,
    image: true,
    direction: true,
});

onMounted(() => {
    bindSelect((select) => {
        enable.value.bold = select;
        enable.value.italic = select;
        enable.value.underline = select;
        enable.value.linethrough = select;
    });
});
</script>

<template>
    <div class="toolbar">
        <div></div>
        <Icon name="undo" :enable="enable.undo" @click="$emit('icon', 'undo')" />
        <Icon name="redo" :enable="enable.redo" @click="$emit('icon', 'redo')" />
        <Icon name="header" :enable="enable.header" @click="$emit('icon', 'header')" />
        <Icon name="bold" :enable="enable.bold" @click="bold(lines)" />
        <Icon name="italic" :enable="enable.italic" @click="italic(lines)" />
        <Icon name="underline" :enable="enable.underline" @click="underline(lines)" />
        <Icon name="linethrough" :enable="enable.linethrough" @click="linethrough(lines)" />
        <Icon name="divider" :enable="enable.divider" @click="newDivider(lines)" />
        <Icon name="quote" :enable="enable.quote" @click="$emit('icon', 'quote')" />
        <Icon name="link" :enable="enable.link" @click="$emit('icon', 'link')" />
        <Icon name="backlog" :enable="enable.backlog" @click="$emit('icon', 'backlog')" />
        <Icon name="image" :enable="enable.image" @click="$emit('icon', 'image')" />
        <Icon name="direction" :enable="enable.direction" @click="$emit('icon', 'direction')" />
        <div></div>
    </div>
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