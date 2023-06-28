<script setup>
import { ref } from 'vue';
import { store } from '../store';
import { url } from '../http';
import { message } from './locale';
import { isEmpty } from './line';
import { focus } from './cursor';
import { compositionStart, compositionEnd } from './composition';
import { keydown } from './keydown';
import { keyup } from './keyup';
import { selectImage, uploadImage, imageName } from './image';

const imageUploader = ref(null);

const compositionend = (event) => {
    compositionEnd(event);
    keyup(event);
};

const className = (line, text) => {
    if (line.id === store.focus && isEmpty(line.texts))
        return 'placeholder';

    let style = text.style;
    if (text.annotation)
        style += ' annotation';

    return style;
};

const innerText = (line, text) => {
    if (line.id === store.focus && isEmpty(line.texts))
        return message('placeholder.' + line.tag);

    return text.text;
};

</script>

<template>
    <div :class="'lines-' + (store.vertical ? 'vertical' : 'horizontal')">
        <div v-for="line in store.lines" :class="'line' + (store.select[line.id] ? ' select' : '')" :key="line.id">
            <h1 v-if="line.tag === 'h1'" :id="line.id" contenteditable="true" @mouseup.stop="focus" @touchend.stop="focus"
                @keydown="keydown" @keyup="keyup" @compositionstart="compositionStart" @compositionend="compositionend">
                <span v-for="(text, index) in line.texts" :class="className(line, text)" :data-index="index">{{
                    innerText(line, text) }}</span>
            </h1>
            <h2 v-else-if="line.tag === 'h2'" :id="line.id" contenteditable="true" @mouseup.stop="focus"
                @touchend.stop="focus" @keydown="keydown" @keyup="keyup" @compositionstart="compositionStart"
                @compositionend="compositionend">
                <span v-for="(text, index) in line.texts" :class="className(line, text)" :data-index="index">{{
                    innerText(line, text) }}</span>
            </h2>
            <h3 v-else-if="line.tag === 'h3'" :id="line.id" contenteditable="true" @mouseup.stop="focus"
                @touchend.stop="focus" @keydown="keydown" @keyup="keyup" @compositionstart="compositionStart"
                @compositionend="compositionend">
                <span v-for="(text, index) in line.texts" :class="className(line, text)" :data-index="index">{{
                    innerText(line, text) }}</span>
            </h3>
            <p v-else-if="line.tag === 'p'" :id="line.id" contenteditable="true" @mouseup.stop="focus"
                @touchend.stop="focus" @keydown="keydown" @keyup="keyup" @compositionstart="compositionStart"
                @compositionend="compositionend">
                <span v-for="(text, index) in line.texts" :class="className(line, text)" :data-index="index">{{
                    innerText(line, text) }}</span>
            </p>
            <div v-else-if="line.tag === 'image'" :id="line.id" class="image" @click="focus">
                <div v-if="line.uploading" class="uploading">{{ line.uploading }}</div>
                <div v-else-if="line.path" class="view">
                    <img :src="url(line.path)" draggable="false" />
                    <div class="name" contenteditable="true" @keyup.stop="imageName">{{ line.name }}
                    </div>
                </div>
                <div v-else class="select" @click="selectImage(imageUploader, $event)">{{ line.upload }}</div>
            </div>
            <div v-else-if="line.tag === 'divider'" :id="line.id" class="divider" @click="focus">
                <div></div>
            </div>
        </div>
    </div>
    <input ref="imageUploader" class="image-uploader" type="file" accept="image/*" multiple @change="uploadImage" />
</template>

<style scoped>
.lines-horizontal {
    margin-left: 32px;
    padding: 4px 8px;
    min-height: calc(100% - 8px);
}

.lines-vertical {
    margin-top: 32px;
    padding: 8px 4px;
    writing-mode: vertical-rl;
    min-width: calc(100% - 8px);
    height: calc(100% - 48px);
}

.lines-horizontal .line {
    padding: 4px 0;
}

.lines-vertical .line {
    padding: 0 4px;
}

.select {
    background-color: var(--select-bg);
}

.line h1,
.line h2,
.line h3,
.line p {
    border: none;
    outline: none;
    margin: 0;
    padding: 0;
    line-height: 32px;
    min-height: 32px;
}

.placeholder {
    color: var(--placeholder);
}

.image .uploading,
.image .select {
    line-height: 250%;
    text-align: center;
    background-color: var(--image-select-bg);
}

.image .select:hover {
    background-color: var(--image-select-hover-bg);
    cursor: pointer;
}

.image-uploader {
    position: absolute;
    top: -100vh;
}
</style>