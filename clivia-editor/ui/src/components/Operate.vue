<script setup>
import { ref, inject, onMounted, nextTick } from 'vue';
import { store } from '../store';
import { now } from './time';
import { message } from './locale';
import { listen } from './event';
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
    tags: ['h1', 'h2', 'h3', 'p', 'img', 'divider'],
});

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
    data.value.more.icon = 'drag';
};

const move = (event) => {
    if (data.value.state != 'drag')
        return;

    if (store.vertical)
        data.value.more.style = { left: event.x - 11 + 'px', top: '10px' };
    else
        data.value.more.style = { left: '10px', top: event.y - 11 - workspace.value.scrollTop + 'px' };
};

const drop = (event) => {
    data.value.more.icon = 'more';
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

onMounted(() => {
    listen('focus', focus);
    listen('scroll', focus);
});

defineExpose({
    move,
    done,
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
            <div v-for="name in data.tags" class="operate">
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
}

.operate {
    display: flex;
    align-items: center;
    padding: 8px;
    cursor: pointer;
}

.operate:hover,
.operate-hover {
    background-color: var(--hover-bg);
    cursor: pointer;
}

.operate img {
    display: block;
    width: 64px;
}

.operate .title-sub {
    padding-left: 8px;
}

.operate .title {
    font-size: 1.25rem;
    color: var(--tag-title);
}

.operate .sub {
    color: var(--tag-sub);
}
</style>