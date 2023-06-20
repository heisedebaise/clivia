<script setup>
import { ref } from 'vue';
import { store } from '../store';
import { now } from './time';
import { findById, isEmpty } from './line';
import { setTag } from './keydown';
import { getFocusId, getCursor, setCursor } from './cursor';
import { message } from './locale';
import Icon from './Icon.vue';
import Ai from './Ai.vue';

const props = defineProps({
    names: Array,
    workspace: Object,
});

const position = ref({
    left: -1,
    top: -1,
    index: -1,
});
const tags = ref(null);
const ai = ref({
    show: ''
});

const show = (node, left, top) => {
    if (!node) {
        position.value.left = left;
        position.value.top = top;

        return;
    }

    let image = node.className === 'image' && node.children[0].className === 'view';
    if (store.vertical) {
        let left = node.offsetLeft - props.workspace.scrollLeft - 320;
        if (image)
            left += node.offsetWidth / 2;
        if (left < 0) {
            if (image)
                left += 320;
            else
                left += node.offsetWidth + 320;
        }
        position.value = {
            left: left,
            top: window.mobile ? 72 : 114,
            index: -1,
        };
    } else {
        let top = (window.mobile ? 0 : 42) + node.offsetTop + node.offsetHeight - props.workspace.scrollTop;
        if (image)
            top -= node.offsetHeight / 2;
        let height = 80 * props.names.length;
        if (top + height > props.workspace.offsetHeight) {
            if (image)
                top -= height;
            else
                top -= node.offsetHeight + height;
            if (top < 42)
                top = 42;
        }
        position.value = {
            left: 72,
            top: top,
            index: -1,
        };
    }
};

const arrow = (e) => {
    if (position.value.left < 0)
        return false;

    if (e.key === 'ArrowDown' && position.value.index < props.names.length - 1) {
        position.value.index++;
    } else if (e.key === 'ArrowUp' && position.value.index > 0) {
        position.value.index--;
    }

    return true;
};

const select = (e) => {
    let id = getFocusId();
    if (id === null)
        return;

    let line = findById(id);
    if (line === null)
        return;

    let name = null;
    if (e && e.target) {
        let node = e.target;
        for (let i = 0; i < 1024; i++) {
            if (node && node.className === 'tag') {
                name = node.dataset.name;

                break;
            }

            node = node.parentElement;
        }
    } else if (position.value.index > -1 && position.value.index < props.names.length) {
        name = props.names[position.value.index];
    }
    if (name != null) {
        let cursor = getCursor();
        if (cursor[1] > 0 && cursor[0] < line.texts.length && cursor[1] <= line.texts[cursor[0]].text.length && slash(line.texts[cursor[0]].text.charAt(cursor[1] - 1))) {
            cursor[1]--;
            cursor[3]--;
            line.texts[cursor[0]].text = line.texts[cursor[0]].text.substring(0, cursor[1]) + line.texts[cursor[0]].text.substring(cursor[1] + 1);
            if (isEmpty(line.texts))
                store.placeholder = line.id;
        }
        setCursor(id, cursor);

        if (name === 'ai-text') {
            ai.value.show = 'text';
        } else if (name === 'ai-image') {
            ai.value.show = 'image';
        } else {
            line.tag = name;
            line.time = now();
        }
    }
    hide();
};

const slash = (ch) => ch === '/' || ch === '、' || ch === '・';

const hide = (e) => {
    if (position.value.left === -1)
        return;

    position.value = {
        left: -1,
        top: -1,
    };
    setCursor();
    setTag(null);
};

defineExpose({
    show,
    arrow,
    select,
    hide,
});
</script>

<template>
    <div v-if="position.left > 0" class="tags-mask" @click="hide">
        <div ref="tags" class="tags" :style="{ left: position.left + 'px', top: position.top + 'px' }">
            <div v-for="(name, index) in names" :class="'tag' + (index === position.index ? ' tag-hover' : '')"
                :data-name="name" @click="select">
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
    <Ai v-if="ai.show" :type="ai.show" @hide="ai.show = ''"></Ai>
</template>

<style>
.tags-mask {
    position: absolute;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
    z-index: 8;
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

.tags .tag:hover,
.tags .tag-hover {
    background-color: var(--hover-bg);
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