<script setup>
import { ref, nextTick, onMounted } from 'vue';
import { store } from '../store';
import { service, url } from '../http';
import { message } from './locale';
import { now } from './time';
import { setAnnotation } from './annotation';
import { findById, findIndex, isEmpty } from './line';
import { focus, focusLast, setCursor } from './cursor';
import { compositionStart, compositionEnd } from './composition';
import { setTag, keydown } from './keydown';
import { keyup } from './keyup';
import { mouseover, dragStart, dragMove, dragDone } from './drag';
import { newText } from './tag';
import { selectImage, uploadImage, imageName } from './image';
import { setDirection } from './workspace';
import Icon from './Icon.vue';
import Tag from './Tag.vue';
import Annotation from './Annotation.vue';

const workspace = ref(null);
const tag = ref(null);
const imageUploader = ref(null);
const annotationRef = ref(null);
const data = ref({
    dragable: {},
    draging: {
        left: -1,
        top: -1,
        html: '',
    },
    tags: ['h1', 'h2', 'h3', 'text'],
    annotations: [],
    annotation: null,
});

const direction = () => {
    store.vertical = !store.vertical;
    data.value.dragable = {};
    annotation();
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

const del = (e) => {
    let node = findDragNode();
    if (node === null)
        return;

    if (store.lines.length === 1) {
        store.lines.splice(0, 1, newText());
    } else {
        store.lines.splice(findIndex(node.id), 1);
    }
    annotation();
};

const plus = (e) => {
    let node = findDragNode();
    if (node === null)
        return;

    tag.value.show(workspace.value, node);
    node.focus();
    setTag(tag.value);
};

const findDragNode = () => {
    for (let line of store.lines) {
        let node = document.querySelector('#' + line.id);
        if (store.vertical) {
            let left = node.offsetLeft - 16;
            if (data.value.dragable.left >= left && data.value.dragable.left < left + node.offsetWidth)
                return node;
        } else {
            if (data.value.dragable.top >= node.offsetTop && data.value.dragable.top < node.offsetTop + node.offsetHeight)
                return node;
        }
    }

    return null;
};

const compositionend = (e) => {
    compositionEnd(e);
    keyup(workspace.value, tag.value, e);
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
    <div ref="workspace" :class="'workspace workspace-' + (store.vertical ? 'vertical' : 'horizontal')" @scroll="scroll"
        @mousemove="dragMove(vertical, data.dragable, data.draging, $event)" @mouseup="dragDone(data.draging, $event)"
        @touchmove="dragMove(vertical, data.dragable, data.draging, $event)" @touchend="dragDone(data.draging, $event)">
        <div v-if="data.dragable.left || data.dragable.top" class="dragable"
            :style="{ left: data.dragable.left + 'px', top: data.dragable.top + 'px', width: data.dragable.width + 'px', height: data.dragable.height + 'px' }">
            <div></div>
            <div class="action">
                <Icon name="delete" @click="del" />
            </div>
            <div class="action" @mousedown="dragStart(vertical, data.draging, $event)"
                @touchstart="dragStart(vertical, data.draging, $event)">
                <Icon name="drag" />
            </div>
            <div class="action">
                <Icon name="plus" @click="plus" />
            </div>
            <div></div>
        </div>
        <div :class="'lines lines-' + (store.vertical ? 'vertical' : 'horizontal')" @click.self="focusLast">
            <div v-for="(line, index) in store.lines" class="line" @mouseover="mouseover(vertical, data.dragable, $event)">
                <h1 v-if="line.tag === 'h1'" :id="line.id" contenteditable="true" @focus.stop="focus(index, $event)"
                    @mouseup.stop="focus(index, $event)" @keydown="keydown" @keyup="keyup(workspace, tag, $event)"
                    @compositionstart="compositionStart" @compositionend="compositionend">
                    <span v-for="(text, i) in line.texts" :class="className(index, i)" :data-index="i">{{
                        innerText(index, i) }}</span>
                </h1>
                <h2 v-else-if="line.tag === 'h2'" :id="line.id" contenteditable="true" @focus.stop="focus(index, $event)"
                    @mouseup.stop="focus(index, $event)" @keydown="keydown" @keyup="keyup(workspace, tag, $event)"
                    @compositionstart="compositionStart" @compositionend="compositionend">
                    <span v-for="(text, i) in line.texts" :class="className(index, i)" :data-index="i">{{
                        innerText(index, i) }}</span>
                </h2>
                <h3 v-else-if="line.tag === 'h3'" :id="line.id" contenteditable="true" @focus.stop="focus(index, $event)"
                    @mouseup.stop="focus(index, $event)" @keydown="keydown" @keyup="keyup(workspace, tag, $event)"
                    @compositionstart="compositionStart" @compositionend="compositionend">
                    <span v-for="(text, i) in line.texts" :class="className(index, i)" :data-index="i">{{
                        innerText(index, i) }}</span>
                </h3>
                <p v-else-if="line.tag === 'text'" :id="line.id" contenteditable="true" @focus.stop="focus(index, $event)"
                    @mouseup.stop="focus(index, $event)" @keydown="keydown" @keyup="keyup(workspace, tag, $event)"
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
    </div>
    <div v-if="data.draging.left >= 0 && data.draging.top >= 0"
        :class="'draging draging-' + (store.vertical ? 'vertical' : 'horizontal')"
        :style="{ left: data.draging.left + 'px', top: data.draging.top + 'px' }" v-html="data.draging.html"></div>
    <Tag ref="tag" :names="data.tags" />
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
    top: 42px;
    right: 0;
    bottom: 0;
    overflow: auto;
}

.workspace .dragable {
    position: absolute;
    display: flex;
    align-items: center;
    justify-content: space-around;
}

.workspace-horizontal .dragable {
    flex-direction: row;
}

.workspace-vertical .dragable {
    flex-direction: column;
}

.workspace .dragable .action:hover {
    border-radius: 4px;
    background-color: var(--hover-bg);
    cursor: pointer;
}

.workspace .lines-horizontal {
    margin-left: 80px;
    min-height: calc(100% - 16px);
}

.workspace .lines-vertical {
    margin-top: 80px;
    min-width: calc(100% - 16px);
    height: calc(100% - 96px);
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

.draging {
    position: absolute;
    color: var(--draging);
    background-color: val(--draging-bg);
}

.draging img {
    opacity: 0.25;
    max-width: 200px;
}

.draging-vertical {
    writing-mode: vertical-rl;
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