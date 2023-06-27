<script setup>
import { ref, inject, nextTick, onMounted } from 'vue';
import { store } from '../store';
import { now } from './time';
import { message } from './locale';
import { listen } from './event';
import { getCursor, setCursor } from './cursor';
import { findLine, splitTexts, isEmpty } from './line';

const workspace = inject('workspace');
const data = ref({
    text: '',
    annotation: '',
    annotations: [],
});

const set = () => {
    let line = findLine(store.focus);
    let texts = splitTexts(line.texts, getCursor());
    let text = '';
    for (let txt of texts[1])
        text += txt.text;
    data.value.text = text;
    delete data.value.reset;
};

const reset = (annotation) => {
    let line = findLine(annotation.id);
    data.value.text = line.texts[annotation.index].text;
    data.value.annotation = annotation.text;
    data.value.reset = annotation;
    setCursor(line.id, [annotation.index, 0, annotation.index, line.texts[annotation.index].text.length]);
};

const ok = () => {
    if (data.value.reset) {
        let line = findLine(data.value.reset.id);
        line.texts[data.value.reset.index].annotation = data.value.annotation;
        line.time = now();
        data.value.text = '';
        annotation();

        return;
    }

    let line = findLine(store.focus);
    let texts = splitTexts(line.texts, getCursor());
    if (isEmpty(texts[1]))
        return;

    texts[1][0].annotation = data.value.annotation;
    for (let text of texts[1])
        texts[0].push(text);
    for (let text of texts[2])
        texts[0].push(text);
    line.texts = texts[0];
    line.time = now();
    setCursor();
    annotation();
    data.value.text = '';
};

const remove = () => {
    let line = findLine(data.value.reset.id);
    delete line.texts[data.value.reset.index].annotation;
    line.time = now();
    data.value.text = '';
    annotation();
};

const annotation = () => {
    nextTick(() => {
        let index = 0;
        let scrollTop = workspace.value.scrollTop;
        let scrollLeft = workspace.value.scrollLeft;
        for (let line of store.lines) {
            if (!line.texts)
                continue;

            for (let i = 0; i < line.texts.length; i++) {
                let text = line.texts[i];
                if (!text.annotation)
                    continue;

                let node = document.querySelector('#' + line.id).children[i];
                let annotation = {
                    id: line.id,
                    index: i,
                    text: text.annotation,
                    left: node.offsetLeft - scrollLeft,
                    top: node.offsetTop - scrollTop,
                };
                if (index < data.value.annotations.length) {
                    data.value.annotations[index] = annotation;
                } else {
                    data.value.annotations.push(annotation);
                }
                index++;
            }
        }
        if (index < data.value.annotations.length)
            data.value.annotations.splice(index, data.value.annotations.length - index);
    });
};

onMounted(() => {
    listen('setAnnotation', set);
    listen('annotation', annotation);
    listen('scroll', annotation);
});
</script>

<template>
    <div v-if="data.text" class="mask" @click.self="data.text = ''">
        <div class="dialog">
            <div class="text">{{ data.text }}</div>
            <div class="input">
                <input v-model="data.annotation" :placeholder="message('annotation.placeholder')" />
            </div>
            <div class="button">
                <div @click="ok">{{ message('annotation.ok') }}</div>
                <div v-if="data.reset" class="remove" @click="remove">{{ message('annotation.remove') }}</div>
            </div>
        </div>
    </div>
    <div v-for="annotation in data.annotations" :class="'annotation-' + (store.vertical ? 'vertical' : 'horizontal')"
        :style="{ left: annotation.left + 'px', top: annotation.top + 'px' }" @click.self="reset(annotation)">{{
            annotation.text }}</div>
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

.text {
    padding: 8px 8px 0 8px;
}

.input {
    padding: 8px;
}

.input input {
    border: none;
    outline: none;
    min-width: 25vw;
}

.button {
    display: flex;
    align-items: center;
    justify-content: center;
    border-top: 1px solid var(--border);
}

.button div {
    flex-grow: 1;
    text-align: center;
    cursor: pointer;
    padding: 8px;
}

.button div:hover {
    background-color: var(--hover-bg);
}

.remove {
    border-left: 1px solid var(--border);
}
</style>