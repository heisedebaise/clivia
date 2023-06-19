<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { store } from '../store';
import { findEventId, findParentByClass } from './event';
import { now } from './time';
import { findIndex } from './line';
import { setTag } from './keydown';
import { annotation } from './annotation';
import Icon from './Icon.vue';
import { message } from './locale';

const props = defineProps({
    workspace: Object,
    tag: Object,
});

const morer = ref(null);
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
        data.value.style.left = e.x - data.value.draging.offset + props.workspace.scrollLeft + 'px';
        data.value.draging.left = node.offsetLeft;
        data.value.draging.top = 72;
    } else {
        data.value.style.top = e.y - data.value.draging.offset + props.workspace.scrollTop + 'px';
        data.value.draging.left = 72;
        data.value.draging.top = node.offsetTop;
    }
};

const done = (e) => {
    if (!data.value.draging.show || !data.value.draging.left || data.value.draging.left < 0 || !data.value.draging.top || data.value.draging.top < 0) {
        data.value.draging = {};

        return;
    }

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
            let left = node.offsetLeft - props.workspace.scrollLeft;
            if (e.x >= left && e.x < left + node.offsetWidth)
                return node;
        } else {
            let top = node.offsetTop - props.workspace.scrollTop + (window.mobile ? 0 : 42);
            if (e.y >= top && e.y < top + node.offsetHeight)
                return node;
        }
    }

    return null;
};

const more = (e) => {
    if (data.value.draging.show)
        return;

    let headers = [];
    for (let line of store.lines) {
        if (line.tag === 'h1' || line.tag === 'h2' || line.tag === 'h3') {
            let text = '';
            for (let t of line.texts)
                text += t.text;
            if (text.length > 16)
                text = text.substring(0, 16) + '...';
            headers.push({
                id: line.id,
                tag: line.tag,
                text,
            });
        }
    }

    data.value.more = {
        show: true,
        headers,
        style: {
            left: '-1000vw',
            top: '-1000vh'
        }
    };

    nextTick(() => {
        let node = findParentByClass(e, 'operate');
        if (store.vertical) {
            let left = e.x - morer.value.offsetWidth - 22;
            if (left <= 0)
                left = e.x + 22;
            data.value.more.style = {
                left: left + 'px',
                top: node.offsetTop + (window.mobile ? 0 : 42) + 'px',
            };

        } else {
            let top = e.y - morer.value.offsetHeight - 22;
            if (top < (window.mobile ? 0 : 42))
                top = e.y + 22;
            data.value.more.style = {
                left: node.offsetLeft + 'px',
                top: top + 'px',
            };
        }
    });
};

const jumpTo = (id) => {
    let node = document.querySelector('#' + id);
    if (node) {
        if (store.vertical)
            props.workspace.scrollLeft = node.offsetLeft + node.offsetWidth - props.workspace.offsetWidth;
        else
            props.workspace.scrollTop = node.offsetTop;
    }
    data.value.more.show = false;
};

const moveUp = (e) => {
    let index = findIndex(data.value.id);
    if (index <= 0)
        return;

    swap(index, index - 1);
};

const moveDown = (e) => {
    let index = findIndex(data.value.id);
    if (index >= store.lines.length - 1)
        return;

    swap(index, index + 1);
};

const swap = (index1, index2) => {
    let line1 = store.lines[index1];
    let line2 = store.lines[index2];
    store.lines[index1] = line2;
    store.lines[index2] = line1;
    line1.time = now();
    line2.time = now();
    data.value.id = line2.id;
    annotation();
    data.value.more.show = false;
}

const remove = (e) => {
    if (store.lines.length === 1) {
        store.lines.splice(0, 1, newText());
    } else {
        store.lines.splice(findIndex(data.value.id), 1);
    }
    annotation();
    data.value.more.show = false;
    window.put(true);
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
        <div class="operate" @click.stop="more">
            <Icon name="more" />
        </div>
        <div class="operate" @click.stop="plus">
            <Icon name="plus" />
        </div>
        <div></div>
    </div>
    <div v-if="data.more.show" class="more-operate-mask" @click.self="data.more.show = false">
        <div ref="morer" class="more-operate" :style="data.more.style">
            <div v-if="data.more.headers.length > 0" class="jump-to">{{ message('operate.jump-to') }}</div>
            <div v-for="header in data.more.headers" :class="'operate operate-' + header.tag"
                @click.self="jumpTo(header.id)">{{ header.text }}</div>
            <div v-if="data.more.headers.length > 0" class="divider"></div>
            <div class="operate" @click="moveUp">{{ message('operate.move.up') }}</div>
            <div class="operate" @click="moveDown">{{ message('operate.move.down') }}</div>
            <div class="divider"></div>
            <div class="operate" @click="remove">{{ message('operate.remove') }}</div>
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

.operates .operate:hover {
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
    padding: 8px 0;
    overflow: hidden;
}

.more-operate .operate {
    padding: 8px 16px;
    cursor: pointer;
    white-space: nowrap;
}

.more-operate .operate:hover {
    background-color: var(--hover-bg);
}

.more-operate .jump-to {
    padding: 0 16px 8px 16px;
    border-bottom: 1px solid var(--border);
    cursor: default;
}

.more-operate .operate-h2 {
    text-indent: 1rem;
}

.more-operate .operate-h3 {
    text-indent: 2rem;
}

.more-operate .divider {
    margin: 8px 0;
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