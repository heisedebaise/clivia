<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { store } from '../store';
import { findEventId } from './event';
import { now } from './time';
import { findIndex } from './line';
import { setTag } from './keydown';
import { annotation } from './annotation';
import Icon from './Icon.vue';
import { message } from './locale';

const props = defineProps({
    tag: Object
});

const data = ref({
    id: '',
    style: {},
    more: {},
    draging: {},
});

const hover = (e) => {
    if (e)
        data.value.id = findEventId(e);
    if (!data.value.id)
        data.value.id = store.lines[0].id;
    let node = document.querySelector('#' + data.value.id);
    if (!node)
        return;

    if (store.vertical) {
        data.value.style = {
            left: node.offsetLeft + 'px',
            top: node.offsetTop - 72 + 'px',
            width: node.offsetWidth + 'px',
            height: '64px',
            flexDirection: 'column',
        };
    } else {
        data.value.style = {
            left: '0',
            top: node.offsetTop + 'px',
            width: '64px',
            height: node.offsetHeight + 'px',
            flexDirection: 'row',
        };
    }
};

const start = () => {
    let node = document.querySelector('#' + data.value.id);
    if (!node)
        return;

    node.blur();
    let offset = 0;
    if (store.vertical) {
        offset = node.offsetWidth / 2;
    } else {
        offset = node.offsetHeight / 2;
        if (!window.mobile)
            offset += 42;
    }
    data.value.draging = {
        show: true,
        left: -1,
        top: -1,
        offset,
        html: node.parentNode.innerHTML,
    };
};

const move = (e) => {
    if (!data.value.draging.show)
        return;

    let node = findDragingNode(e);
    if (node === null)
        return;

    data.value.draging.id = node.children[0].id;
    if (store.vertical) {
        data.value.style.left = e.x - data.value.draging.offset + 'px';
        data.value.draging.left = node.offsetLeft;
        data.value.draging.top = 72;
    } else {
        data.value.style.top = e.y - data.value.draging.offset + 'px';
        data.value.draging.left = 72;
        data.value.draging.top = node.offsetTop;
    }
};

const done = (e) => {
    if (!data.value.draging.show)
        return;

    let source = findIndex(data.value.id);
    let target = findIndex(data.value.draging.id);
    let line = store.lines[source];
    line.time = now();
    if (source > target) {
        store.lines.splice(source, 1);
        store.lines.splice(target, 0, line);
    } else if (source < target) {
        store.lines.splice(target, 0, line);
        store.lines.splice(source, 1);
    }
    annotation();
    setTimeout(() => data.value.draging = {}, 10);
};

const findDragingNode = (e) => {
    if (e.touches && e.touches.length > 0) {
        e.x = e.touches[0].pageX;
        e.y = e.touches[0].pageY;
    }
    for (let node of document.querySelectorAll('.line')) {
        if (store.vertical) {
            if (e.x >= node.offsetLeft && e.x < node.offsetLeft + node.offsetWidth)
                return node;
        } else {
            let top = node.offsetTop + (window.mobile ? 0 : 42);
            if (e.y >= top && e.y < top + node.offsetHeight)
                return node;
        }
    }

    return null;
};

const more = () => {
    data.value.more.show = true;
};

const plus = () => {
    if (data.value.draging.show)
        return;

    let node = document.querySelector('#' + data.value.id);
    if (!node)
        return;

    props.tag.show(node);
    node.focus();
    setTag(props.tag);
};

onMounted(() => {
    hover();
});

defineExpose({
    hover,
    move,
    done,
});
</script>

<template>
    <div class="operates" :style="data.style" @mousedown="start" @touchstart="start">
        <div></div>
        <div class="operate" @click="more">
            <Icon name="more" />
        </div>
        <div class="operate" @click="plus">
            <Icon name="plus" />
        </div>
        <div></div>
    </div>
    <div v-if="data.more.show" class="more-operate-mask" @click.self="data.more.show = false">
        <div class="more-operate" :style="{ top: data.more.top }">
            <div>{{ message('operate.move.up') }}</div>
            <div>{{ message('operate.move.down') }}</div>
            <div class="divider"></div>
            <div>{{ message('operate.delete') }}</div>
        </div>
    </div>
    <div v-if="data.draging.show && data.draging.left > 0 && data.draging.top > 0"
        :class="'draging draging-' + (store.vertical ? 'vertical' : 'horizontal')"
        :style="{ left: data.draging.left + 'px', top: data.draging.top + 'px' }" v-html="data.draging.html"></div>
</template>

<style scoped>
.operates {
    position: absolute;
    display: flex;
    align-items: center;
    justify-content: space-around;
    cursor: pointer;
}

.operate:hover {
    border-radius: 4px;
    overflow: hidden;
    background-color: var(--hover-bg);
}

.more-operate-mask {
    position: fixed;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
}

.more-operate {
    position: fixed;
    left: 8px;
    border: 1px solid var(--border);
    background-color: var(--background);
    border-radius: 8px;
}

.more-operate .divider {
    border-top: 1px solid var(--border);
}

.draging {
    position: absolute;
    color: var(--draging);
    background-color: val(--draging-bg);
}

.draging-horizontal {
    transform: translateY(-50%);
}

.draging-vertical {
    writing-mode: vertical-rl;
    transform: translateX(50%);
}

.draging img {
    opacity: 0.25;
    max-width: 200px;
}
</style>