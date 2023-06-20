<script setup>
import { ref, onMounted, vShow, nextTick } from 'vue';
import { setTag } from './keydown';
import { bindSelect } from './cursor';
import { bold, italic, underline, linethrough } from './style';
import { newImage, newDivider } from './tag';
import { historyListener, historyBack, historyForward } from './history';
import { direction } from './workspace';
import Icon from './Icon.vue';
import Tag from './Tag.vue';
import Annotation from './Annotation.vue';

const emits = defineEmits(['icon']);

const style = ref({
    className: 'toolbar',
    position: {},
});

const enable = ref({
    undo: false,
    redo: false,
    header: true,
    bold: false,
    italic: false,
    underline: false,
    linethrough: false,
    annotation: false,
    search: true,
    divider: true,
    quote: false,
    link: false,
    backlog: false,
    image: true,
    direction: true,
});

const undo = () => {
    if (enable.value.undo)
        historyBack();
};

const redo = () => {
    if (enable.value.redo)
        historyForward();
};

const tag = ref(null);

const header = (e) => {
    let offset = findOffset(e);
    tag.value.show(null, offset.left, offset.top);
    setTag(tag.value);
};

const annotation = ref(null);

const showAnnotation = (e) => {
    let offset = findOffset(e);
    annotation.value.show(offset.left, offset.top, '', window.mobile ? 'xy' : 'x');
};

const findOffset = (e) => {
    let left = -1;
    let top = -1;
    let node = e.target;
    for (let i = 0; i < 1024; i++) {
        if (!node)
            break;

        if (node.className && typeof node.className === 'string') {
            if (node.className.indexOf('icon') > -1) {
                left = node.offsetLeft;
            } else if (node.className.indexOf('toolbar') === 0) {
                top = window.mobile ? (node.offsetTop + 1) : (node.offsetHeight - 1);

                break;
            }
        }

        node = node.parentNode;
    }

    return { left, top };
};

const resize = (e) => {
    if (!window.mobile)
        return;

    nextTick(() => {
        style.value = {
            className: 'toolbar toolbar-mobile',
            position: {
                top: window.visualViewport.height - 42 + 'px',
            },
        };
    });
};

onMounted(() => {
    resize();
    if (window.visualViewport)
        window.visualViewport.addEventListener('resize', resize);
    else
        window.addEventListener('resize', resize);

    historyListener((undo, redo) => {
        enable.value.undo = undo;
        enable.value.redo = redo;
    });
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
    <div :class="style.className" :style="style.position">
        <div></div>
        <Icon name="undo" :enable="enable.undo" @click="undo" />
        <Icon name="redo" :enable="enable.redo" @click="redo" />
        <Icon name="header" :enable="enable.header" @click="header" />
        <Icon name="bold" :enable="enable.bold" @click="bold" />
        <Icon name="italic" :enable="enable.italic" @click="italic" />
        <Icon name="underline" :enable="enable.underline" @click="underline" />
        <Icon name="linethrough" :enable="enable.linethrough" @click="linethrough" />
        <Icon name="annotation" :enable="enable.annotation" @click="showAnnotation" />
        <Icon name="search" :enable="enable.search" @click="" />
        <Icon name="divider" :enable="enable.divider" @click="newDivider" />
        <Icon name="quote" :enable="enable.quote" @click="$emit('icon', 'quote')" />
        <Icon name="link" :enable="enable.link" @click="$emit('icon', 'link')" />
        <Icon name="image" :enable="enable.image" @click="newImage()" />
        <Icon name="direction" :enable="enable.direction" @click="direction" />
        <div></div>
    </div>
    <Tag ref="tag" :names="['h1', 'h2', 'h3']" />
    <Annotation ref="annotation" />
</template>

<style scoped>
.toolbar {
    display: flex;
    align-items: center;
    justify-content: space-around;
    border-top: 1px solid var(--border);
    border-bottom: 1px solid var(--border);
    height: 40px;
    background-color: var(--background);
}

.toolbar-mobile {
    position: fixed;
    left: 0;
    right: 0;
}

.toolbar .icon-enable:hover {
    background-color: var(--hover-bg);
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