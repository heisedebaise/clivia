<script setup>
import { ref, onMounted } from 'vue';
import { store } from '../store';
import { service, url } from '../http';
import { message } from './locale';
import { getFocusId } from './cursor';
import { findIndex } from './line';
import { newText } from './tag';
import { newImage } from './tag';
import { annotation } from './annotation';

const props = defineProps({
    type: String
});

const emits = defineEmits(['hide']);

const description = ref(null);
const reply = ref([]);
const empty = ref('');

const get = (e) => {
    reply.value = [];
    empty.value = message('ai.wait');
    service('/editor/ai-' + props.type, { content: description.value.innerText, count: e.target.dataset.count }, data => {
        if (data === '') {
            empty.value = message('ai.empty');

            return;
        }

        empty.value = '';
        for (let d of data.split('\n')) {
            d = d.trim();
            if (d.length > 0)
                reply.value.push(d);
        }
    })
};

const text = () => {
    if (reply.value.length === 0)
        return;

    let id = getFocusId();
    if (id === null)
        return;

    let index = findIndex(id);
    for (let text of reply.value)
        store.lines.splice(++index, 0, newText(text));
    annotation();
    emits('hide');
};

const image = (e) => {
    if (reply.value.length === 0)
        return;

    newImage(reply.value[e.target.dataset.index], message('ai.generate'));
    emits('hide');
};

onMounted(() => {
    setTimeout(() => description.value.focus(), 250);
});
</script>

<template>
    <div class="ai-mask" @click="$emit('hide')">
        <div class="ai" @click.stop="">
            <div class="description">
                <div ref="description" class="input" contenteditable="true"></div>
                <div v-if="type === 'text'" class="go" @click="get">{{ message('ai.go') }}</div>
                <div v-if="type === 'image'" class="go" data-count="1" @click="get">{{ message('ai.go.1') }}</div>
                <div v-if="type === 'image'" class="go" data-count="2" @click="get">{{ message('ai.go.2') }}</div>
                <div v-if="type === 'image'" class="go" data-count="4" @click="get">{{ message('ai.go.4') }}</div>
            </div>
            <div v-if="type === 'text' && reply.length > 0" class="text">
                <div class="reply">
                    <p v-for="text in reply">{{ text }}</p>
                </div>
                <div class="insert" @click="text">{{ message('ai.insert') }}</div>
            </div>
            <div v-if="type === 'image' && reply.length > 0" class="image">
                <div v-if="reply.length === 1" class="one">
                    <img :src="url(reply[0])" />
                    <div class="insert" data-index="0" @click="image">{{ message('ai.insert') }}</div>
                </div>
                <div v-if="reply.length > 1">
                    <div v-for="(n, i) in ((reply.length >> 1) + (reply.length % 2))">
                        <div class="two">
                            <div>
                                <img :src="url(reply[2 * i])" />
                                <div class="insert" :data-index="2 * i" @click="image">{{ message('ai.insert') }}</div>
                            </div>
                            <div v-if="2 * i + 1 < reply.length">
                                <img :src="url(reply[2 * i + 1])" />
                                <div class="insert" :data-index="2 * i + 1" @click="image">{{ message('ai.insert') }}</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div v-if="empty" class="empty">{{ empty }}</div>
        </div>
    </div>
</template>

<style>
.ai-mask {
    position: absolute;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
    z-index: 8;
    background-color: var(--mask-bg);
}

.ai {
    position: fixed;
    left: 50vw;
    top: 20vh;
    background-color: var(--background);
    border: 1px solid var(--border);
    border-radius: 4px;
    overflow: hidden;
    transform: translateX(-50%);
}

.ai .description {
    display: flex;
    align-items: stretch;
}

.ai .description .input {
    width: 50vw;
    border: none;
    outline: none;
    padding: 8px;
}

.ai .description .go {
    display: flex;
    align-items: center;
    padding: 0 8px;
    border-left: 1px solid var(--border);
}

.ai .description .go:hover {
    cursor: pointer;
    background-color: var(--hover-bg);
}

.ai .text,
.ai .image,
.ai .empty {
    border-top: 1px solid var(--border);
}

.ai .text .reply {
    max-height: 50vh;
    overflow: auto;
    padding: 8px 16px;
}

.ai .text .reply p {
    margin: 0;
    padding: 0;
    line-height: 150%;
}

.ai .text .insert {
    border-top: 1px solid var(--border);
    text-align: center;
    line-height: 150%;
}

.ai .text .insert:hover {
    cursor: pointer;
    background-color: var(--hover-bg);
}

.ai .image {
    max-height: 50vh;
    overflow: auto;
    padding: 8px;
}

.ai .image .one {
    padding: 8px;
}

.ai .image .one img {
    display: block;
    width: 100%;
}

.ai .image .two {
    display: flex;
    justify-content: space-between;
    padding: 8px;
}

.ai .image .two>div {
    width: calc(50% - 8px);
}

.ai .image .two img {
    display: block;
    width: 100%;
}

.ai .image .insert {
    text-align: center;
    line-height: 1.5rem;
    background-color: var(--hover-bg);
}

.ai .image .insert:hover {
    cursor: pointer;
    background-color: var(--hover-bg);
}

.ai .empty {
    padding: 8px;
    text-align: center;
    color: var(--empty);
}
</style>