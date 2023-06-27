<script setup>
import { ref, onMounted } from 'vue';
import { store } from '../store';
import { message } from './locale';
import { listen } from './event';
import { mergeTexts } from './line';
import Icon from './Icon.vue';

const data = ref({
    show: false,
    find: '',
    finds: [],
    replace: '',
    result: '',
    prev: false,
    next: false,
    one: false,
    all: false,
});

const show = () => {
    data.value.show = true;
    find();
};

const find = () => {
    reset();
    if (data.value.find === '') {
        data.value.result = message('search.empty');

        return;
    }

    let finds = [];
    for (let i = 0; i < store.lines.length; i++) {
        let line = store.lines[i];
        if (!line.texts || line.texts.length === 0)
            continue;

        for (let j = 0; j < line.texts.length; j++) {
            let text = line.texts[j];
            let indexOf = text.text.indexOf(data.value.find);
            if (indexOf === -1)
                continue;

            if (!text.style)
                text.style = '';
            line.texts.splice(j++, 0, { text: text.text.substring(0, indexOf), style: text.style });
            line.texts.splice(j, 0, { text: data.value.find, style: text.style + ' find' });
            text.text = text.text.substring(indexOf + data.value.find.length);
            finds.push([i, j]);
        }
    }
    if (finds.length === 0)
        data.value.result = message('search.empty');
    else {
        data.value.result = '?/' + finds.length;
        data.value.prev = true;
        data.value.next = true;
        data.value.one = true;
        data.value.all = true;
        data.value.finds = finds;
    }
};

const close = () => {
    data.value.show = false;
    reset();
};

const reset = () => {
    for (let line of store.lines) {
        if (!line.texts || line.texts.length === 0)
            continue;

        let found = false;
        for (let text of line.texts) {
            if (text.style && text.style.indexOf('find') > -1) {
                text.style = text.style.replace(/find/g, '');
                found = true;
            }
        }
        if (found)
            mergeTexts(line.texts);
    }
};

onMounted(() => {
    listen('search', show);
});
</script>

<template>
    <div v-if="data.show" class="search">
        <div class="line">
            <input class="input" :placeholder="message('search.find')" v-model="data.find" @input="find" />
            <div class="result">{{ data.result }}</div>
            <div :class="'button ' + (data.prev ? 'enable' : '')">
                <Icon name="arrow-up" :enable="data.prev" />
            </div>
            <div :class="'button ' + (data.next ? 'enable' : '')">
                <Icon name="arrow-down" :enable="data.next" />
            </div>
            <div class="button enable" @click="close">
                <Icon name="close" :enable="true" />
            </div>
        </div>
        <div class="line">
            <input class="input" :placeholder="message('search.replace')" v-model="data.replace" />
            <div :class="'button ' + (data.one ? 'enable' : '')">
                <Icon name="replace" :enable="data.one" />
            </div>
            <div :class="'button ' + (data.all ? 'enable' : '')">
                <Icon name="replace-all" :enable="data.all" />
            </div>
        </div>
    </div>
</template>

<style scoped>
.search {
    position: fixed;
    top: 8px;
    right: 8px;
    background-color: var(--background);
    border: 1px solid var(--border);
    border-radius: 4px;
    overflow: hidden;
    box-shadow: var(--shadow);
    padding: 8px;
}

.line {
    display: flex;
    align-items: center;
}

.input {
    border: 1px solid var(--border);
    outline: none;
    border-radius: 4px;
    padding: 4px;
    margin-right: 4px;
}

.result {
    padding: 0 4px;
}

.button {
    padding: 2px;
}

.enable {
    cursor: pointer;
}

.enable:hover {
    background-color: var(--hover-bg);
}
</style>