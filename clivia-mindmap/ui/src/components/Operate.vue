<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { store } from '../store';
import { listen, trigger } from './event';
import { message } from './locale';
import { focus } from './cursor';
import { newNode, removeNode, setIndex } from './node';
import Icon from './Icon.vue';

const data = ref({
    show: false,
    style: '',
    main: false,
    link: false,
    linkTo: false,
    linkIndex: '',
});

const show = (event) => {
    nextTick(() => {
        let operate = find(event);
        if (!operate)
            return;

        let node = store.node();
        data.value = {
            show: true,
            style: 'left:' + operate.offsetLeft + 'px;top:' + (operate.offsetTop + operate.offsetHeight) + 'px',
            main: node.main,
            link: node.link,
        };
    });
};

const find = (event) => {
    let node = event.target;
    for (let i = 0; i < 1024; i++) {
        if (!node)
            break;

        if (node.className === 'operate')
            return node;

        node = node.parentElement;
    }

    return null;
};

const insert = () => {
    let node = store.node();
    if (!node.children)
        node.children = [];
    let child = newNode(node.id);
    node.children.push(child.id);
    focus(child.id);
    setIndex(node.id);
    trigger('branch', { type: 'new', id: node.id });
    trigger('link');
    hide();
};

const link = () => {
    data.value.show = false;
    data.value.linkTo = true;
    data.value.linkIndex = '';
};

const linkTo = () => {
    let node = store.node();
    if (node) {
        for (let id in store.nodes) {
            if (store.nodes[id].index === data.value.linkIndex) {
                node.link = id;
                trigger('link');

                break;
            }
        }
    }
    hide();
};

const unlink = () => {
    let node = store.node();
    if (node) {
        delete node.link;
        trigger('link');
    }
    hide();
};

const remove = () => {
    removeNode(store.node());
    trigger('link');
    hide();
};

const hide = () => {
    data.value = {};
    focus();
};

onMounted(() => {
    listen('operate', show);
});
</script>

<template>
    <div v-if="data.show" class="mask" @click.self="hide">
        <div class="operates" :style="data.style">
            <div class="operate" @click="insert">
                <Icon name="plus" :enable="true" />
                <div>{{ message('operate.add') }}</div>
            </div>
            <div v-if="data.link" class="operate" @click="unlink">
                <Icon name="link" :enable="true" />
                <div>{{ message('operate.unlink') }}</div>
            </div>
            <div v-else class="operate" @click="link">
                <Icon name="link" :enable="true" />
                <div>{{ message('operate.link') }}</div>
            </div>
            <div v-if="!data.main" class="operate" @click="remove">
                <Icon name="delete" :enable="true" />
                <div>{{ message('operate.remove') }}</div>
            </div>
        </div>
    </div>
    <div v-if="data.linkTo" class="mask" @click.self="hide">
        <div class="link-to" :style="data.style">
            <div class="title">{{ message('operate.link') }}</div>
            <div class="input">
                <input v-model="data.linkIndex" :placeholder="message('operate.link.placeholder')" />
            </div>
            <div class="button" @click="linkTo">{{ message('operate.link.ok') }}</div>
        </div>
    </div>
</template>

<style scoped>
.mask {
    position: fixed;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
}

.operates,
.link-to {
    position: fixed;
    background-color: var(--background);
    border: 1px solid var(--border);
    box-shadow: var(--shadow);
    border-radius: 4px;
}

.operate {
    display: flex;
    align-items: center;
    padding: 4px 8px 4px 0;
}

.operate:hover,
.button:hover {
    background-color: var(--hover-bg);
    cursor: pointer;
}

.title,
.button {
    text-align: center;
    padding: 4px 0;
}

.title {
    background-color: var(--title-bg);
    border-bottom: 1px solid var(--border);
}

.input input {
    border: none;
    outline: none;
    padding: 8px;
}

.button {
    border-top: 1px solid var(--border);
}
</style>