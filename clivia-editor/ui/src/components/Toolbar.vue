<script setup>
import Icon from './Icon.vue';

const props = defineProps({
    workspace: Object
});

const click = () => {
    workspace.value.direction();
};
</script>

<template>
    <div class="toolbar">
        <div></div>
        <Icon name="undo" @click="$emit('icon', 'undo')" />
        <Icon name="redo" @click="$emit('icon', 'redo')" />
        <Icon name="header" @click="$emit('icon', 'header')" />
        <Icon name="bold" @click="$emit('icon', 'bold')" />
        <Icon name="italic" @click="$emit('icon', 'italic')" />
        <Icon name="underline" @click="$emit('icon', 'underline')" />
        <Icon name="strikethrough" @click="$emit('icon', 'strikethrough')" />
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