<script setup>
import { ref, onMounted } from 'vue';
import { service } from '../http';
import { findIndex } from './line';
import { focus } from './cursor';
import { setTag, keydown } from './keydown';
import { setAnnotation, compositionstart, compositionend, keyup } from './keyup';
import { mouseover, mousedown, mousemove, mouseup } from './drag';
import { newText } from './tag';
import { selectImage, uploadImage } from './image';
import Icon from './Icon.vue';
import Tag from './Tag.vue';

const props = defineProps({
    editable: Boolean,
    lines: Array,
});

const vertical = ref(false);

const toolbar = (action) => {
    if (action === 'direction') {
        vertical.value = !vertical.value;
        annotation();
    }
};

const workspace = ref(null);
const dragable = ref({
    left: -1,
    top: -1,
    height: 0,
});
const draging = ref({
    left: -1,
    top: -1,
    html: '',
});
const tag = ref(null);
const tagNames = ref(['h1', 'h2', 'h3', 'text']);
const imageUploader = ref(null);
const annotations = ref([]);

const annotation = () => {
    setTimeout(() => {
        let index = 0;
        let scrollTop = workspace.value.scrollTop;
        let scrollLeft = workspace.value.scrollLeft;
        for (let line of props.lines) {
            if (!line.texts)
                continue;

            for (let i = 0; i < line.texts.length; i++) {
                let text = line.texts[i];
                if (!text.annotation)
                    continue;

                let node = document.querySelector('#' + line.id).children[i];
                let annotation = {
                    text: text.annotation,
                    left: node.offsetLeft - scrollLeft,
                    top: node.offsetTop - scrollTop + 42,
                };
                if (index < annotations.value.length) {
                    annotations.value[index] = annotation;
                } else {
                    annotations.value.push(annotation);
                }
                index++;
            }
        }
        if (index < annotations.value.length)
            annotations.value.splice(index + 1, annotations.value.length - index);
    }, 10);
};
setAnnotation(annotation);

const scroll = (e) => {
    annotation();
};

const del = (e) => {
    let node = findDragNode();
    if (node === null)
        return;

    if (props.lines.length === 1) {
        props.lines.splice(0, 1, newText());
    } else {
        props.lines.splice(findIndex(props.lines, node.id), 1);
    }
    annotation();
};

const plus = (e) => {
    let node = findDragNode();
    if (node === null)
        return;

    if (vertical.value) {
        tag.value.show(node.offsetLeft, 122);
    } else {
        tag.value.show(80, 42 + node.offsetTop + node.offsetHeight);
    }
    node.focus();
    setTag(tag.value);
};

const findDragNode = () => {
    for (let line of props.lines) {
        let node = document.querySelector('#' + line.id);
        if (vertical.value) {
            let left = node.offsetLeft - 16;
            if (dragable.value.left >= left && dragable.value.left < left + node.offsetWidth)
                return node;
        } else {
            if (dragable.value.top >= node.offsetTop && dragable.value.top < node.offsetTop + node.offsetHeight)
                return node;
        }
    }

    return null;
};

onMounted(() => {
    annotation();
    service('/editor/ai', {}, data => {
        if (data) {
            tagNames.value.push('ai-text');
            tagNames.value.push('ai-image');
        }
    });
});

defineExpose({
    toolbar,
    annotation,
});
</script>

<template>
    <div ref="workspace" :class="'workspace workspace-' + (vertical ? 'vertical' : 'horizontal')" @scroll="scroll"
        @mousemove="mousemove(vertical, dragable, draging, $event)" @mouseup="mouseup(lines, draging, $event)">
        <div v-if="dragable.left >= 0 && dragable.top >= 0" class="dragable"
            :style="{ left: dragable.left + 'px', top: dragable.top + 'px', height: dragable.height + 'px' }">
            <div></div>
            <div class="action">
                <Icon name="delete" @click="del" />
            </div>
            <div class="action" @mousedown="mousedown(vertical, draging, $event)">
                <Icon name="drag" />
            </div>
            <div class="action">
                <Icon name="plus" @click="plus" />
            </div>
            <div></div>
        </div>
        <div class="editing-area" @mousemove.stop="">
            <div v-for="line in lines" class="line" @mouseover="mouseover(vertical, dragable, $event)">
                <h1 v-if="line.tag === 'h1'" :id="line.id" :contenteditable="editable" :class="line.className"
                    :placeholder="line.placeholder" @focus.stop="focus" @mouseup.stop="focus"
                    @keydown="keydown(lines, $event)" @keyup="keyup(lines, vertical, tag, $event)"
                    @compositionstart="compositionstart" @compositionend="compositionend(lines, vertical, tag, $event)">
                    <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
                </h1>
                <h2 v-else-if="line.tag === 'h2'" :id="line.id" :contenteditable="editable" :class="line.className"
                    :placeholder="line.placeholder" @focus.stop="focus" @mouseup.stop="focus"
                    @keydown="keydown(lines, $event)" @keyup="keyup(lines, vertical, tag, $event)"
                    @compositionstart="compositionstart" @compositionend="compositionend(lines, vertical, tag, $event)">
                    <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
                </h2>
                <h3 v-else-if="line.tag === 'h3'" :id="line.id" :contenteditable="editable" :class="line.className"
                    :placeholder="line.placeholder" @focus.stop="focus" @mouseup.stop="focus"
                    @keydown="keydown(lines, $event)" @keyup="keyup(lines, vertical, tag, $event)"
                    @compositionstart="compositionstart" @compositionend="compositionend(lines, vertical, tag, $event)">
                    <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
                </h3>
                <p v-else-if="line.tag === 'text'" :id="line.id" :contenteditable="editable" :class="line.className"
                    :placeholder="line.placeholder" @focus.stop="focus" @mouseup.stop="focus"
                    @keydown="keydown(lines, $event)" @keyup="keyup(lines, vertical, tag, $event)"
                    @compositionstart="compositionstart" @compositionend="compositionend(lines, vertical, tag, $event)">
                    <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
                </p>
                <div v-else-if="line.tag === 'image'" :id="line.id" class="image">
                    <div v-if="line.uploading" class="uploading">{{ line.uploading }}</div>
                    <div v-else-if="line.path">
                        <img :src="line.url" draggable="false" />
                        <div class="name">{{ line.name }}</div>
                    </div>
                    <div v-else class="select" @click="selectImage(imageUploader, $event)">{{ line.upload }}</div>
                </div>
                <div v-else-if="line.tag === 'divider'" :id="line.id"
                    :class="(vertical ? 'vertical' : 'horizontal') + '-divider'">
                    <div></div>
                </div>
            </div>
        </div>
    </div>
    <div v-if="draging.left >= 0 && draging.top >= 0" :class="'draging draging-' + (vertical ? 'vertical' : 'horizontal')"
        :style="{ left: draging.left + 'px', top: draging.top + 'px' }" v-html="draging.html"></div>
    <Tag ref="tag" :names="tagNames" :lines="lines" :vertical="vertical" />
    <input ref="imageUploader" class="image-uploader" type="file" accept="image/*" multiple
        @change="uploadImage(lines, $event)" />
    <div v-for="annotation in annotations" :class="'annotation-' + (vertical ? 'vertical' : 'horizontal')"
        :style="{ left: annotation.left + 'px', top: annotation.top + 'px' }">{{ annotation.text }}</div>
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

.dragable {
    position: absolute;
}

.dragable .action:hover {
    border-radius: 4px;
    background-color: var(--icon-hover-bg);
    cursor: pointer;
}

.workspace .dragable {
    display: flex;
    align-items: center;
    justify-content: space-around;
    width: 80px;
}

.workspace-vertical .dragable {
    transform: rotate(90deg);
}

.editing-area {
    padding: 8px;
}

.workspace-horizontal .editing-area {
    margin-left: 80px;
    min-height: calc(100% - 16px);
}

.workspace-vertical .editing-area {
    margin-top: 80px;
    min-width: calc(100% - 16px);
    height: calc(100% - 96px);
    writing-mode: vertical-rl;
}

.line>* {
    margin: 0;
    padding: 0;
    border: none;
    outline: none;
}

/* .line .empty {
    content: attr(data-placeholder);
    color: blueviolet;
} */

.line>h1,
.line>h2,
.line>h3,
.line>p {
    min-height: 3rem;
    line-height: 3rem;
}

.line>.image img {
    display: block;
}

.workspace-horizontal .line>.image img {
    max-width: 100%;
}

.workspace-vertical .line>.image img {
    max-height: 100%;
}

.line>.image .name {
    background-color: var(--image-name-bg);
    text-align: center;
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

.line>.horizontal-divider {
    padding: 0.5rem 0;
}

.line>.horizontal-divider>div {
    border-top: 1px solid var(--border);
}

.line>.vertical-divider {
    padding: 0 0.5rem;
}

.line>.vertical-divider>div {
    border-right: 1px solid var(--border);
}

.line .bold {
    font-weight: bold;
}

.line .italic {
    font-style: italic;
}

.line .underline {
    text-decoration: underline;
}

.line .linethrough {
    text-decoration: line-through;
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

.annotation-horizontal {
    position: absolute;
    transform: translateY(-100%);
    color: var(--annotation);
}

.annotation-vertical {
    position: absolute;
    transform: translateX(1rem);
    color: var(--annotation);
    writing-mode: vertical-rl;
}
</style>