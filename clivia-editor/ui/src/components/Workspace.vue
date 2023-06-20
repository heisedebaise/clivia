<script setup>
import { ref, nextTick, onMounted } from 'vue';
import { store } from '../store';
import { service, url } from '../http';
import { message } from './locale';
import { now } from './time';
import { setAnnotation } from './annotation';
import { findById, isEmpty } from './line';
import { focus, focusLast, setCursor } from './cursor';
import { compositionStart, compositionEnd } from './composition';
import { keydown } from './keydown';
import { keyup } from './keyup';
import { selectImage, uploadImage, imageName } from './image';
import { setDirection } from './workspace';
import Toolbar from './Toolbar.vue';
import Tag from './Tag.vue';
import Annotation from './Annotation.vue';
import Operate from './Operate.vue';

const operate = ref(null);
const workspace = ref(null);
const tag = ref(null);
const imageUploader = ref(null);
const annotationRef = ref(null);
const data = ref({
    className: '',
    tags: ['h1', 'h2', 'h3', 'text'],
    annotations: [],
    annotation: null,
});

const direction = () => {
    store.vertical = !store.vertical;
    annotation();
    nextTick(() => operate.value.hover());
};
setDirection(direction);

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
                    top: node.offsetTop - scrollTop + 42,
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
setAnnotation(annotation);

const scroll = (e) => {
    annotation();
};

const compositionend = (e) => {
    compositionEnd(e);
    keyup(tag.value, e);
};

const className = (index, i) => {
    let line = store.lines[index];
    if (line.id === store.placeholder && isEmpty(line.texts))
        return 'placeholder';

    let style = line.texts[i].style;
    if (line.texts.length > i && line.texts[i].annotation)
        style += ' annotation';

    return style;
};

const innerText = (index, i) => {
    let line = store.lines[index];
    if (line.id === store.placeholder && isEmpty(line.texts))
        return message('placeholder.' + line.tag);

    return line.texts[i].text;
};

const showAnnotation = (annotation) => {
    data.value.annotation = annotation;
    annotationRef.value.show(annotation.left, annotation.top, annotation.text, 'y');
    setCursor(annotation.id, [annotation.index, 0, annotation.index, annotation.text.length]);
};

const unsetAnnotation = () => {
    if (!data.value.annotation)
        return;

    let line = findById(data.value.annotation.id);
    if (!line || line.texts.length <= data.value.annotation.index)
        return;

    let text = line.texts[data.value.annotation.index];
    delete text.annotation;
    line.time = now();
    annotation();
};

onMounted(() => {
    data.value.className = 'workspace workspace-' + (window.mobile ? 'mobile' : 'desktop');
    if (store.lines.length === 1 && isEmpty(store.lines[0].texts)) {
        store.placeholder = store.lines[0].id;
    }
    annotation();
    service('/editor/ai', {}, data => {
        if (data) {
            data.value.tags.push('ai-text');
            data.value.tags.push('ai-image');
        }
    });
});
</script>

<template>
    <div ref="workspace" :class="data.className" @scroll="scroll" @mousemove="operate.move" @mouseup="operate.done"
        @touchmove="operate.move" @touchend="operate.done">
        <div :class="'lines lines-' + (store.vertical ? 'vertical' : 'horizontal')" @click.self="focusLast">
            <div v-for="(line, index) in store.lines" class="line" @mouseover="operate.hover">
                <h1 v-if="line.tag === 'h1'" :id="line.id" contenteditable="true" @focus.stop="focus(index, $event)"
                    @mouseup.stop="focus(index, $event)" @keydown="keydown" @keyup="keyup(tag, $event)"
                    @compositionstart="compositionStart" @compositionend="compositionend">
                    <span v-for="(text, i) in line.texts" :class="className(index, i)" :data-index="i">{{
                        innerText(index, i) }}</span>
                </h1>
                <h2 v-else-if="line.tag === 'h2'" :id="line.id" contenteditable="true" @focus.stop="focus(index, $event)"
                    @mouseup.stop="focus(index, $event)" @keydown="keydown" @keyup="keyup(tag, $event)"
                    @compositionstart="compositionStart" @compositionend="compositionend">
                    <span v-for="(text, i) in line.texts" :class="className(index, i)" :data-index="i">{{
                        innerText(index, i) }}</span>
                </h2>
                <h3 v-else-if="line.tag === 'h3'" :id="line.id" contenteditable="true" @focus.stop="focus(index, $event)"
                    @mouseup.stop="focus(index, $event)" @keydown="keydown" @keyup="keyup(tag, $event)"
                    @compositionstart="compositionStart" @compositionend="compositionend">
                    <span v-for="(text, i) in line.texts" :class="className(index, i)" :data-index="i">{{
                        innerText(index, i) }}</span>
                </h3>
                <p v-else-if="line.tag === 'text'" :id="line.id" contenteditable="true" @focus.stop="focus(index, $event)"
                    @mouseup.stop="focus(index, $event)" @keydown="keydown" @keyup="keyup(tag, $event)"
                    @compositionstart="compositionStart" @compositionend="compositionend">
                    <span v-for="(text, i) in line.texts" :class="className(index, i)" :data-index="i">{{
                        innerText(index, i) }}</span>
                </p>
                <div v-else-if="line.tag === 'image'" :id="line.id" class="image">
                    <div v-if="line.uploading" class="uploading">{{ line.uploading }}</div>
                    <div v-else-if="line.path" class="view">
                        <img :src="url(line.path)" draggable="false" />
                        <div class="name" contenteditable="true" @keyup.stop="imageName">{{ line.name }}
                        </div>
                    </div>
                    <div v-else class="select" @click="selectImage(imageUploader, $event)">{{ line.upload }}</div>
                </div>
                <div v-else-if="line.tag === 'divider'" :id="line.id" class="divider">
                    <div></div>
                </div>
            </div>
        </div>
        <Toolbar />
        <Operate ref="operate" :workspace="workspace" :tag="tag" />
    </div>
    <Tag ref="tag" :names="data.tags" :workspace="workspace" />
    <input ref="imageUploader" class="image-uploader" type="file" accept="image/*" multiple @change="uploadImage" />
    <div v-for="annotation in data.annotations" :class="'annotation-' + (store.vertical ? 'vertical' : 'horizontal')"
        :style="{ left: annotation.left + 'px', top: annotation.top + 'px' }" @click.self="showAnnotation(annotation)">{{
            annotation.text }}</div>
    <Annotation ref="annotationRef" @unset="unsetAnnotation" />
</template>

<style>
.workspace {
    position: absolute;
    left: 0;
    right: 0;
    overflow: auto;
}

.workspace-desktop {
    top: 42px;
    bottom: 0;
}

.workspace-mobile {
    top: 0;
    bottom: 0;
}

.workspace .lines-horizontal {
    margin-left: 64px;
    min-height: calc(100% - 16px);
}

.workspace .lines-vertical {
    margin-top: 64px;
    min-width: calc(100% - 16px);
    height: calc(100% - 80px);
}

.line>.image .uploading,
.line>.image .select {
    line-height: 250%;
    text-align: center;
    background-color: var(--image-select-bg);
}

.line>.image .select:hover {
    background-color: var(--image-select-hover-bg);
    cursor: pointer;
}

.line .placeholder {
    color: var(--placeholder);
}

.line .annotation {
    background-color: var(--hover-bg);
}

.image-uploader {
    position: absolute;
    top: -100vh;
}

.annotation-vertical,
.annotation-horizontal {
    font-size: 0.5rem;
}
</style>