<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { store } from '../store';
import { listen, trigger } from './event';
import { message } from './locale';
import { focus } from './cursor';
import { newNode, removeNode, setIndex, branch } from './node';
import Icon from './Icon.vue';

const data = ref({
    show: false,
    style: '',
    main: false,
    link: false,
    linkTo: false,
    up: false,
    down: false,
    moveTo: false,
    index: '',
});

const show = (event) => {
    nextTick(() => {
        let operate = find(event);
        if (!operate)
            return;

        let node = store.node();
        let d = {
            show: true,
            style: 'left:' + operate.offsetLeft + 'px;top:' + (operate.offsetTop + operate.offsetHeight) + 'px',
            main: node.main,
            link: node.link,
        };

        if (!node.main) {
            let parent = store.nodes[node.parent];
            if (parent) {
                let index = parent.children.indexOf(node.id);
                if (index > 0)
                    d.up = true;
                if (index < parent.children.length - 1)
                    d.down = true;
            }
        }

        data.value = d
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
    branch();
    trigger('link');
    hide();
};

const link = () => {
    data.value.show = false;
    data.value.linkTo = true;
    data.value.index = '';
};

const linkTo = () => {
    let node = store.node();
    if (node) {
        for (let id in store.nodes) {
            if (store.nodes[id].index === data.value.index) {
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

const up = () => {
    let node = store.node();
    if (node && node.parent) {
        let parent = store.nodes[node.parent];
        if (parent) {
            let index = parent.children.indexOf(node.id);
            if (index > 0) {
                let children = parent.children.join(',').split(',');
                children[index] = children[index - 1];
                children[index - 1] = node.id;
                parent.children = children;
                setIndex(parent.id);
                nextTick(() => {
                    branch();
                    trigger('link');
                });
            }
        }
    }
    hide();
};

const down = () => {
    let node = store.node();
    if (node && node.parent) {
        let parent = store.nodes[node.parent];
        if (parent) {
            let index = parent.children.indexOf(node.id);
            if (index < parent.children.length - 1) {
                let children = parent.children.join(',').split(',');
                children[index] = children[index + 1];
                children[index + 1] = node.id;
                parent.children = children;
                setIndex(parent.id);
                nextTick(() => {
                    branch();
                    trigger('link');
                });
            }
        }
    }
    hide();
};

const move = () => {
    data.value.show = false;
    data.value.moveTo = true;
    data.value.index = '';
};

const moveTo = () => {
    let node = store.node();
    if (node) {
        for (let id in store.nodes) {
            let target = store.nodes[id];
            if (target.index === data.value.index) {
                let parent = store.nodes[node.parent];
                if (parent && parent.children && parent.children.length > 0) {
                    let index = parent.children.indexOf(node.id);
                    if (index > -1) {
                        parent.children.splice(index, 1);
                        setIndex(parent.id);
                        branch();
                    }
                }
                let children = target.children || [];
                children.push(node.id);
                target.children = children;
                node.parent = target.id;
                setIndex(id);
                nextTick(() => {
                    branch();
                    trigger('link');
                });

                break;
            }
        }
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
            <div v-if="data.up" class="operate" @click="up">
                <Icon name="up" :enable="true" />
                <div>{{ message('operate.move.up') }}</div>
            </div>
            <div v-if="data.down" class="operate" @click="down">
                <Icon name="down" :enable="true" />
                <div>{{ message('operate.move.down') }}</div>
            </div>
            <div v-if="!data.main" class="operate" @click="move">
                <Icon name="link" :enable="true" />
                <div>{{ message('operate.move.to') }}</div>
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
                <input v-model="data.index" :placeholder="message('operate.link.placeholder')" />
            </div>
            <div class="button" @click="linkTo">{{ message('operate.link.ok') }}</div>
        </div>
    </div>
    <div v-if="data.moveTo" class="mask" @click.self="hide">
        <div class="move-to" :style="data.style">
            <div class="title">{{ message('operate.move.to') }}</div>
            <div class="input">
                <input v-model="data.index" :placeholder="message('operate.move.placeholder')" />
            </div>
            <div class="button" @click="moveTo">{{ message('operate.move.ok') }}</div>
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
.link-to,
.move-to {
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