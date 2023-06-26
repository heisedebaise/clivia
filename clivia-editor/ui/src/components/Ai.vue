<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { store } from '../store';
import { service, url } from '../http';
import { listen, trigger } from './event';
import { newText, newImage } from './tag';
import { message } from './locale';
import { findIndex } from './line';

const data = ref({
    type: '',
    reply: [],
    empty: '',
});
const description = ref(null);

const get = (event) => {
    data.value.reply = [];
    data.value.empty = message('ai.wait');
    service('/editor/ai-' + data.value.type, { content: description.value.innerText, count: event.target.dataset.count }, dt => {
        if (dt === '') {
            data.value.empty = message('ai.empty');

            return;
        }

        data.value.empty = '';
        for (let d of dt.split('\n')) {
            d = d.trim();
            if (d.length > 0)
                data.value.reply.push(d);
        }
    })
};

const text = () => {
    if (data.value.reply.length === 0)
        return;

    let index = findIndex(store.focus);
    for (let text of data.value.reply)
        store.lines.splice(++index, 0, newText('p', text));
    trigger('annotation');
    data.value.type = '';
};

const image = (event) => {
    if (data.value.reply.length === 0)
        return;

    newImage(data.value.reply[event.target.dataset.index], message('ai.generate'));
    trigger('annotation');
    data.value.type = '';
};

const show = (event) => {
    data.value = {
        type: event.type,
        reply: [],
        empty: '',
    };
    nextTick(() => description.value.focus());
};

onMounted(() => {
    listen('ai', show);
});
</script>

<template>
    <div v-if="data.type" class="mask" @click.self="data.type = ''">
        <div class="dialog">
            <div class="description">
                <div ref="description" class="input" contenteditable="true"></div>
                <div v-if="data.type === 'text'" class="go" @click="get">{{ message('ai.go') }}</div>
                <div v-if="data.type === 'image'" class="go" data-count="1" @click="get">{{ message('ai.go.1') }}</div>
                <div v-if="data.type === 'image'" class="go" data-count="2" @click="get">{{ message('ai.go.2') }}</div>
                <div v-if="data.type === 'image'" class="go" data-count="4" @click="get">{{ message('ai.go.4') }}</div>
            </div>
            <div v-if="data.type === 'text' && data.reply.length > 0" class="text">
                <div class="reply">
                    <p v-for="text in data.reply">{{ text }}</p>
                </div>
                <div class="insert" @click="text">{{ message('ai.insert') }}</div>
            </div>
            <div v-if="data.type === 'image' && data.reply.length > 0" class="image">
                <div v-if="data.reply.length === 1" class="one">
                    <img :src="url(data.reply[0])" />
                    <div class="insert" data-index="0" @click="image">{{ message('ai.insert') }}</div>
                </div>
                <div v-if="data.reply.length > 1">
                    <div v-for="(n, i) in ((data.reply.length >> 1) + (data.reply.length % 2))">
                        <div class="two">
                            <div>
                                <img :src="url(data.reply[2 * i])" />
                                <div class="insert" :data-index="2 * i" @click="image">{{ message('ai.insert') }}</div>
                            </div>
                            <div v-if="2 * i + 1 < data.reply.length">
                                <img :src="url(data.reply[2 * i + 1])" />
                                <div class="insert" :data-index="2 * i + 1" @click="image">{{ message('ai.insert') }}</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div v-if="data.empty" class="empty">{{ data.empty }}</div>
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

.dialog {
    position: fixed;
    left: 50vw;
    top: 50vh;
    transform: translate(-50%, -50%);
    background-color: var(--background);
    border: 1px solid var(--border);
    border-radius: 4px;
    overflow: hidden;
    box-shadow: var(--shadow);
}

.description {
    display: flex;
    align-items: stretch;
}

.description .input {
    width: 50vw;
    border: none;
    outline: none;
    padding: 8px;
}

.description .go {
    display: flex;
    align-items: center;
    padding: 0 8px;
    border-left: 1px solid var(--border);
}

.description .go:hover {
    cursor: pointer;
    background-color: var(--hover-bg);
}

.text,
.image,
.empty {
    border-top: 1px solid var(--border);
}

.text .reply {
    max-height: 50vh;
    overflow: auto;
    padding: 8px 16px;
}

.text .reply p {
    margin: 0;
    padding: 0;
    line-height: 150%;
}

.text .insert {
    border-top: 1px solid var(--border);
    text-align: center;
    line-height: 150%;
}

.text .insert:hover {
    cursor: pointer;
    background-color: var(--hover-bg);
}

.image {
    max-height: 50vh;
    overflow: auto;
    padding: 8px;
}

.image .one {
    padding: 8px;
}

.image .one img {
    display: block;
    width: 100%;
}

.image .two {
    display: flex;
    justify-content: space-between;
    padding: 8px;
}

.image .two>div {
    width: calc(50% - 8px);
}

.image .two img {
    display: block;
    width: 100%;
}

.image .insert {
    text-align: center;
    line-height: 1.5rem;
    background-color: var(--hover-bg);
}

.image .insert:hover {
    cursor: pointer;
    background-color: var(--hover-bg);
}

.empty {
    padding: 8px;
    text-align: center;
    color: var(--empty);
}
</style>