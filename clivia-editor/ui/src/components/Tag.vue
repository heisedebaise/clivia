<script setup>
import { ref } from 'vue';
import { now } from './time';
import { findById } from './line';
import { getFocusId, setCursor } from './cursor';
import { message } from './locale';
import Icon from './Icon.vue';

const props = defineProps({
    names: Array,
    lines: Array,
});

const position = ref({
    left: -1,
    top: -1,
});

const show = (left, top) => {
    position.value = { left, top };
    setCursor();
};

const select = (e) => {
    let id = getFocusId();
    if (id === null)
        return;

    let line = findById(props.lines, id);
    if (line === null)
        return;

    let node = e.target;
    for (let i = 0; i < 1024; i++) {
        if (node.className === 'tag') {
            line.tag = node.dataset.name;
            line.time = now();
            setCursor(id);

            break;
        }

        node = node.parentElement;
    }
};

const hide = (e) => {
    position.value = {
        left: -1,
        top: -1,
    };
    setCursor();
};

defineExpose({
    show,
});
</script>

<template>
    <div v-if="position.left > 0" class="tags-mark" @click="hide">
        <div class="tags" :style="{ left: position.left + 'px', top: position.top + 'px' }">
            <div v-for="name in names" class="tag" :data-name="name" @click="select">
                <div class="image">
                    <Icon :name="name" />
                </div>
                <div class="title-sub">
                    <div class="title">{{ message(`tag.${name}`) }}</div>
                    <div class="sub">{{ message(`tag.${name}.sub`) }}</div>
                </div>
            </div>
        </div>
    </div>
</template>

<style>
.tags-mark {
    position: absolute;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
}

.tags {
    position: absolute;
    background-color: var(--background);
    border: 1px solid var(--border);
    border-radius: 4px;
    overflow: hidden;
}

.tags .tag {
    display: flex;
    align-items: center;
    padding: 4px 12px;
    cursor: pointer;
}

.tags .tag:hover {
    background-color: var(--icon-hover-bg);
    cursor: pointer;
}

.tags .tag img {
    display: block;
    width: 64px;
}

.tags .tag .title-sub {
    padding-left: 8px;
}

.tags .tag .title {
    font-size: 1.25rem;
    color: var(--tag-title);
}

.tags .tag .sub {
    color: var(--tag-sub);
}
</style>