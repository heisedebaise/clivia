<script setup>
import { ref, inject, onMounted, nextTick } from 'vue';
import { store } from '../store';
import { service } from '../http';
import { now } from './time';
import { message } from './locale';
import { listen, trigger } from './event';
import { findIndex, findLine } from './line';
import { newText, newImage, newDivider } from './tag';
import { getCursor, setCursor } from './cursor';
import { setStyleName } from './style';
import { historyEnable, historyBack, historyForward } from './history';
import Icon from './Icon.vue';

const workspace = inject('workspace');
const data = ref({
    state: '',
    time: 0,
    more: {
        style: {},
        icon: 'more',
    },
    operates: {},
    enable: {
        undo: false,
        redo: false,
        image: true,
        link: false,
        quote: false,
        backlog: false,
        divider: true,
        search: true,
        direction: true,
        bold: true,
        italic: true,
        underline: true,
        linethrough: true,
        annotation: false,
        ai: false,
        top: false,
        up: false,
        down: false,
        bottom: false,
        delete: true,
    },
    headers: [],
    draging: {},
});
const colors = ['Default', 'Gray', 'Brown', 'Orange', 'Yellow', 'Green', 'Blue', 'Purple', 'Pink', 'Red'];

const start = (event) => {
    data.value.state = 'start';
    data.value.time = now();
    setTimeout(() => {
        if (data.value.state === 'start') {
            data.value.state = 'drag';
            drag(event);
        }
    }, 250);
};

const done = (event) => {
    if (data.value.state === 'start') {
        data.value.state = 'operates';
        nextTick(() => operates(event));
    } else if (data.value.state === 'drag') {
        data.value.state = 'drop';
        drop(event);
    }
};

const operates = (event) => {
    data.value.enable.undo = historyEnable(true);
    data.value.enable.redo = historyEnable(false);
    let cursor = getCursor();
    let range = cursor[0] != cursor[2] || cursor[1] != cursor[3];
    data.value.enable.annotation = range;
    let index = findIndex(store.focus);
    data.value.enable.top = index > 0;
    data.value.enable.up = index > 0;
    data.value.enable.down = index < store.lines.length - 1;
    data.value.enable.bottom = index < store.lines.length - 1;

    let headers = [];
    for (let line of store.lines) {
        if (line.tag === 'h1' || line.tag === 'h2' || line.tag === 'h3') {
            let text = '';
            for (let txt of line.texts)
                text += txt.text;
            if (text.length > 16)
                text = text.substring(0, 16) + '...';
            headers.push({ id: line.id, tag: line.tag, text });
        }
    }
    data.value.headers = headers;

    let operates = document.querySelector('.operates');
    if (store.vertical) {
        let left = event.x - operates.offsetWidth - 22;
        if (left < 0)
            left = event.x + 22;
        data.value.operates = { left: left + 'px', top: '10px' };
    } else {
        let top = event.y - operates.offsetHeight - 22;
        if (top < 0)
            top = event.y + 22;
        data.value.operates = { left: '10px', top: top + 'px' };
    }
};

const drag = (event) => {
    let node = document.querySelector('#' + store.focus);
    if (!node)
        return;

    data.value.more.icon = 'drag';
    data.value.draging.className = 'draging draging-' + (store.vertical ? 'vertical' : 'horizontal');
    data.value.draging.html = node.parentElement.innerHTML;
    move(event);
};

const move = (event) => {
    if (data.value.state != 'drag')
        return;

    if (store.vertical) {
        data.value.more.style = { left: event.x - 11 + 'px', top: '10px' };
        findDrag(event);
        let node = document.querySelector('#' + data.value.draging.id).parentElement;
        data.value.draging.style = { left: node.offsetLeft - workspace.value.scrollLeft + (data.value.draging.last ? node.offsetWidth : 0) + 'px', top: '40px' };
    } else {
        data.value.more.style = { left: '10px', top: event.y - 11 + 'px' };
        findDrag(event);
        let node = document.querySelector('#' + data.value.draging.id).parentElement;
        data.value.draging.style = { left: '40px', top: node.offsetTop - workspace.value.scrollTop + (data.value.draging.last ? node.offsetHeight : 0) + 'px' };
    }
};

const findDrag = (event) => {
    data.value.draging.id = '';
    data.value.draging.last = false;
    if (store.vertical) {
        for (let line of store.lines) {
            data.value.draging.id = line.id;
            let node = document.querySelector('#' + line.id).parentElement;
            let left = node.offsetLeft - workspace.value.scrollLeft;
            if (event.x >= left && event.x < left + node.offsetWidth)
                return;
        }
    } else {
        for (let line of store.lines) {
            data.value.draging.id = line.id;
            let node = document.querySelector('#' + line.id).parentElement;
            let top = node.offsetTop - workspace.value.scrollTop;
            if (event.y >= top && event.y < top + node.offsetHeight)
                return;
        }
    }
    data.value.draging.last = true;
};

const drop = (event) => {
    data.value.more.icon = 'more';
    if (data.value.draging.id === store.focus) {
        data.value.draging = {};

        return;
    }

    let index = findIndex(store.focus);
    let line = store.lines[index];
    if (data.value.draging.last) {
        store.lines.splice(index, 1);
        store.lines.push(line);
    } else {
        let target = findIndex(data.value.draging.id);
        if (index > target) {
            store.lines.splice(index, 1);
            store.lines.splice(target, 0, line);
        } else {
            store.lines.splice(index, 1);
            store.lines.splice(target - 1, 0, line);
        }
    }
    data.value.draging = {};
    setCursor(store.focus, [0, 0, 0, 0]);
    trigger('annotation');
    window.put(true);
};

const focus = () => {
    let node = document.querySelector('#' + store.focus);
    if (!node)
        return;

    if (store.vertical)
        data.value.more.style = { left: node.offsetLeft + (node.offsetWidth - 22) / 2 - workspace.value.scrollLeft + 'px', top: '10px' };
    else
        data.value.more.style = { left: '10px', top: node.offsetTop + (node.offsetHeight - 22) / 2 - workspace.value.scrollTop + 'px' };
};

const undo = () => {
    if (!data.value.enable.undo)
        return;

    historyBack();
    trigger('annotation');
    data.value.state = '';
};

const redo = () => {
    if (!data.value.enable.redo)
        return;

    historyForward();
    trigger('annotation');
    data.value.state = '';
};

const image = () => {
    newImage();
    data.value.state = '';
};

const divider = () => {
    newDivider();
    data.value.state = '';
};

const direction = () => {
    store.vertical = !store.vertical;
    data.value.state = '';
    nextTick(() => {
        focus();
        setCursor(store.focus, getCursor());
    });
    trigger('annotation');
};

const setStyle = (style) => {
    setStyleName(style);
    data.value.state = '';
};

const setAnnotation = () => {
    let cursor = getCursor();
    if (cursor[0] === cursor[2] && cursor[1] === cursor[3])
        return;

    setCursor(store.focus, cursor);
    trigger('setAnnotation');
    data.value.state = '';
};

const changeTag = (tag) => {
    let line = findLine(store.focus);
    if (!line)
        return;

    line.tag = tag;
    if ((tag === 'h1' || tag === 'h2' || tag === 'h3' || tag === 'p') && !line.texts)
        line.texts = [{ text: '' }];
    line.time = now();
    setCursor(store.focus, getCursor());
    data.value.state = '';
    trigger('annotation');
};

const ai = (type) => {
    trigger('ai', { type });
    data.value.state = '';
};

const top = () => {
    let index = findIndex(store.focus);
    if (index <= 0)
        return;

    let line = store.lines[index];
    store.lines.splice(index, 1);
    store.lines.splice(0, 0, line);
    setCursor(store.focus, getCursor());
    data.value.state = '';
    trigger('annotation');
    window.put(true);
};

const up = () => {
    let index = findIndex(store.focus);
    if (index <= 0)
        return;

    let line = store.lines[index];
    store.lines[index] = store.lines[index - 1];
    store.lines[index - 1] = line;
    setCursor(store.focus, getCursor());
    data.value.state = '';
    trigger('annotation');
    window.put(true);
};

const down = () => {
    let index = findIndex(store.focus);
    if (index >= store.lines.length - 1)
        return;

    let line = store.lines[index];
    store.lines[index] = store.lines[index + 1];
    store.lines[index + 1] = line;
    setCursor(store.focus, getCursor());
    data.value.state = '';
    trigger('annotation');
    window.put(true);
};

const bottom = () => {
    let index = findIndex(store.focus);
    if (index >= store.lines.length - 1)
        return;

    let line = store.lines[index];
    store.lines.splice(index, 1);
    store.lines.push(line);
    setCursor(store.focus, getCursor());
    data.value.state = '';
    trigger('annotation');
    window.put(true);
};

const remove = () => {
    let index = findIndex(store.focus);
    if (store.lines.length === 1)
        store.lines.splice(index, 1, newText());
    else
        store.lines.splice(index, 1);
    if (index <= store.lines.length - 1)
        store.focus = store.lines[index].id;
    else
        store.focus = store.lines[index - 1].id;
    setCursor(store.focus, [0, 0, 0, 0]);
    data.value.state = '';
    trigger('annotation');
    window.put(true);
};

const forground = (index) => {
    setStyleName('forground-' + colors[index].toLocaleLowerCase());
    data.value.state = '';
};

const background = (index) => {
    setStyleName('background-' + colors[index].toLocaleLowerCase());
    data.value.state = '';
};

const jumpTo = (id) => {
    let node = document.querySelector('#' + id);
    if (node) {
        if (store.vertical)
            workspace.value.scrollLeft = node.offsetLeft + node.offsetWidth - workspace.value.offsetWidth;
        else
            workspace.value.scrollTop = node.offsetTop;
    }
    store.focus = id;
    setCursor(id, [0, 0, 0, 0]);
    data.value.state = '';
};

onMounted(() => {
    listen('focus', focus);
    listen('scroll', focus);
    listen('move', move);
    listen('drop', done);
    service('/editor/ai', {}, data => {
        if (data)
            data.value.enable.ai = true;
    });
});
</script>

<template>
    <div v-if="data.more.style.left || data.more.style.top" class="more" :style="data.more.style" @mousedown="start"
        @touchstart="start">
        <Icon v-if="data.more.icon === 'drag'" name="drag" :enable="true" />
        <Icon v-else name="more" />
    </div>
    <div v-if="data.state === 'operates'" class="mask" @click.self="data.state = ''">
        <div class="operates" :style="data.operates">
            <div class="line">
                <div :class="'operate' + (data.enable.undo ? ' enable' : '')" @click="undo">
                    <Icon name="undo" :enable="data.enable.undo" />
                </div>
                <div :class="'operate' + (data.enable.redo ? ' enable' : '')" @click="redo">
                    <Icon name="redo" :enable="data.enable.redo" />
                </div>
                <div :class="'operate' + (data.enable.image ? ' enable' : '')" @click="image">
                    <Icon name="image" :enable="data.enable.image" />
                </div>
                <div :class="'operate' + (data.enable.link ? ' enable' : '')">
                    <Icon name="link" :enable="data.enable.link" />
                </div>
                <div :class="'operate' + (data.enable.divider ? ' enable' : '')" @click="divider">
                    <Icon name="divider" :enable="data.enable.divider" />
                </div>
                <div :class="'operate' + (data.enable.search ? ' enable' : '')">
                    <Icon name="search" :enable="data.enable.search" />
                </div>
                <div :class="'operate' + (data.enable.direction ? ' enable' : '')" @click="direction">
                    <Icon name="direction" :enable="data.enable.direction" />
                </div>
            </div>
            <div class="line">
                <div :class="'operate' + (data.enable.bold ? ' enable' : '')" @click="setStyle('bold')">
                    <Icon name="bold" :enable="data.enable.bold" />
                </div>
                <div :class="'operate' + (data.enable.italic ? ' enable' : '')" @click="setStyle('italic')">
                    <Icon name="italic" :enable="data.enable.italic" />
                </div>
                <div :class="'operate' + (data.enable.underline ? ' enable' : '')" @click="setStyle('underline')">
                    <Icon name="underline" :enable="data.enable.underline" />
                </div>
                <div :class="'operate' + (data.enable.linethrough ? ' enable' : '')" @click="setStyle('linethrough')">
                    <Icon name="linethrough" :enable="data.enable.linethrough" />
                </div>
                <div :class="'operate' + (data.enable.annotation ? ' enable' : '')" @click="setAnnotation">
                    <Icon name="annotation" :enable="data.enable.annotation" />
                </div>
                <div :class="'operate' + (data.enable.quote ? ' enable' : '')">
                    <Icon name="quote" :enable="data.enable.quote" />
                </div>
                <div :class="'operate' + (data.enable.backlog ? ' enable' : '')">
                    <Icon name="backlog" :enable="data.enable.backlog" />
                </div>
            </div>
            <div class="line">
                <div :class="'operate' + (data.enable.h1 ? ' enable' : '')" @click="changeTag('h1')">
                    <Icon name="h1" :enable="data.enable.h1" />
                </div>
                <div :class="'operate' + (data.enable.h2 ? ' enable' : '')" @click="changeTag('h2')">
                    <Icon name="h2" :enable="data.enable.h2" />
                </div>
                <div :class="'operate' + (data.enable.h3 ? ' enable' : '')" @click="changeTag('h3')">
                    <Icon name="h3" :enable="data.enable.h3" />
                </div>
                <div :class="'operate' + (data.enable.p ? ' enable' : '')" @click="changeTag('p')">
                    <Icon name="p" :enable="data.enable.p" />
                </div>
            </div>
            <div class="line">
                <div :class="'operate' + (data.enable.top ? ' enable' : '')" @click="top">
                    <Icon name="top" :enable="data.enable.top" />
                </div>
                <div :class="'operate' + (data.enable.up ? ' enable' : '')" @click="up">
                    <Icon name="up" :enable="data.enable.up" />
                </div>
                <div :class="'operate' + (data.enable.down ? ' enable' : '')" @click="down">
                    <Icon name="down" :enable="data.enable.down" />
                </div>
                <div :class="'operate' + (data.enable.bottom ? ' enable' : '')" @click="bottom">
                    <Icon name="bottom" :enable="data.enable.bottom" />
                </div>
                <div :class="'operate' + (data.enable.delete ? ' enable' : '')" @click="remove">
                    <Icon name="delete" :enable="data.enable.delete" />
                </div>
            </div>
            <div v-if="data.enable.ai" class="title">{{ message('operate.ai') }}</div>
            <div v-if="data.enable.ai" class="line">
                <div class="ai" @click="ai('text')">{{ message('operate.ai.text') }}</div>
                <div class="ai" @click="ai('image')">{{ message('operate.ai.image') }}</div>
            </div>
            <div class="title">{{ message('operate.forground') }}</div>
            <div class="colors">
                <div v-for="index in 5" :class="'forground forground-' + colors[index - 1].toLocaleLowerCase()"
                    @click="forground(index - 1)">{{ colors[index - 1].substring(0, 1) }}</div>
            </div>
            <div class="colors">
                <div v-for="index in 5" :class="'forground forground-' + colors[index + 4].toLocaleLowerCase()"
                    @click="forground(index + 4)">{{ colors[index + 4].substring(0, 1) }}</div>
            </div>
            <div class="title">{{ message('operate.background') }}</div>
            <div class="colors">
                <div v-for="index in 5" :class="'background background-' + colors[index - 1].toLocaleLowerCase()"
                    @click="background(index - 1)">{{ colors[index - 1].substring(0, 1) }}</div>
            </div>
            <div class="colors">
                <div v-for="index in 5" :class="'background background-' + colors[index + 4].toLocaleLowerCase()"
                    @click="background(index + 4)">{{ colors[index + 4].substring(0, 1) }}</div>
            </div>
            <div v-if="data.headers.length > 0" class="title">{{ message('operate.jump-to') }}</div>
            <div v-for="header in data.headers" :class="'jump-to jump-to-' + header.tag" @click="jumpTo(header.id)">{{
                header.text
            }}</div>
        </div>
    </div>
    <div v-if="data.draging.html" :class="data.draging.className" :style="data.draging.style" v-html="data.draging.html">
    </div>
</template>

<style scoped>
.more {
    position: absolute;
    width: 22px;
    height: 22px;
    cursor: pointer;
}

.more:hover {
    background-color: var(--hover-bg);
}

.mask {
    position: fixed;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
}

.operates {
    position: fixed;
    background-color: var(--background);
    border: 1px solid var(--border);
    border-radius: 4px;
    overflow: hidden;
    box-shadow: var(--shadow);
}

.line {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 4px 8px;
}

.enable:hover {
    background-color: var(--hover-bg);
    cursor: pointer;
}

.blank {
    width: 16px;
}

.title {
    padding: 4px 8px;
    cursor: default;
    background-color: var(--hover-bg);
}

.ai {
    border: 1px solid var(--border);
    padding: 4px 8px;
    border-radius: 4px;
    cursor: pointer;
}

.ai:hover {
    background-color: var(--hover-bg);
}

.colors {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 4px 8px;
}

.forground,
.background {
    border: 1px solid var(--border);
    padding: 4px;
    border-radius: 4px;
    cursor: pointer;
}

.forground:hover {
    background-color: var(--hover-bg);
}

.jump-to:hover {
    background-color: var(--hover-bg);
    cursor: pointer;
}

.jump-to-h1 {
    padding: 4px 8px;
}

.jump-to-h2 {
    padding: 4px 8px 4px 12px;
}

.jump-to-h3 {
    padding: 4px 8px 4px 16px;
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
    transform: translateX(20%);
    writing-mode: vertical-rl;
}
</style>