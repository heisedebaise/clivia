<script setup>
import { bold, italic, underline, linethrough } from './style';
import Icon from './Icon.vue';

const props = defineProps({
    lines: Array,
    workspace: Object
});
</script>

<template>
    <div class="toolbar">
        <div></div>
        <Icon name="undo" @click="$emit('icon', 'undo')" />
        <Icon name="redo" @click="$emit('icon', 'redo')" />
        <Icon name="header" @click="$emit('icon', 'header')" />
        <Icon name="bold" @click="bold(lines)" />
        <Icon name="italic" @click="italic(lines)" />
        <Icon name="underline" @click="underline(lines)" />
        <Icon name="linethrough" @click="linethrough(lines)" />
        <Icon name="divider" @click="$emit('icon', 'divider')" />
        <Icon name="quote" @click="$emit('icon', 'quote')" />
        <Icon name="link" @click="$emit('icon', 'link')" />
        <Icon name="backlog" @click="$emit('icon', 'backlog')" />
        <Icon name="image" @click="$emit('icon', 'image')" />
        <Icon name="direction" @click="$emit('icon', 'direction')" />
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

.toolbar .icon img {
    width: 16px;
    height: 16px;
}

.toolbar .icon:hover {
    background-color: var(--icon-hover);
    border-radius: 4px;
    cursor: pointer;
}
</style>