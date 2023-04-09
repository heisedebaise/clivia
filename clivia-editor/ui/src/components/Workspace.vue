<script setup>
import { ref } from 'vue';
import { focus } from './cursor';
import { compositionstart, compositionend } from './composition';
import { keydown } from './keydown';
import { keyup } from './keyup';
import { mouseover, mousemove } from './drag';
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
});
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
                    top: node.offsetTop - scrollTop,
                };
                if (vertical.value) {
                    annotation.top += 122;
                } else {
                    annotation.left += 80;
                    annotation.top += 42;
                }
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

const scroll = (e) => {
    annotation();
};

defineExpose({
    toolbar,
    annotation,
});
</script>

<template>
    <div ref="workspace" :class="'workspace workspace-' + (vertical ? 'vertical' : 'horizontal')" @scroll="scroll"
        @mousemove="mousemove">
        <div v-if="dragable.left >= 0 && dragable.top >= 0" class="dragable"
            :style="{ left: dragable.left + 'px', top: dragable.top + 'px' }">
            <div>
                <Icon name="drag" />
            </div>
            <div>
                <Icon name="plus" />
            </div>
        </div>
        <div class="editing-area" @mousemove.stop="">
            <div v-for="line in lines" class="line" @mouseover="mouseover(lines, vertical, dragable, $event)">
                <h1 v-if="line.tag === 'h1'" :id="line.id" :contenteditable="editable" :class="line.className"
                    :placeholder="line.placeholder" @focus.stop="focus" @mouseup.stop="focus"
                    @keydown="keydown(lines, $event)" @keyup="keyup(lines, $event)" @compositionstart="compositionstart"
                    @compositionend="compositionend">
                    <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
                </h1>
                <h2 v-else-if="line.tag === 'h2'" :id="line.id" :contenteditable="editable" :class="line.className"
                    :placeholder="line.placeholder" @focus.stop="focus" @mouseup.stop="focus"
                    @keydown="keydown(lines, $event)" @keyup="keyup(lines, $event)" @compositionstart="compositionstart"
                    @compositionend="compositionend">
                    <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
                </h2>
                <h3 v-else-if="line.tag === 'h3'" :id="line.id" :contenteditable="editable" :class="line.className"
                    :placeholder="line.placeholder" @focus.stop="focus" @mouseup.stop="focus"
                    @keydown="keydown(lines, $event)" @keyup="keyup(lines, $event)" @compositionstart="compositionstart"
                    @compositionend="compositionend">
                    <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
                </h3>
                <p v-else-if="line.tag === 'text'" :id="line.id" :contenteditable="editable" :class="line.className"
                    :placeholder="line.placeholder" @focus.stop="focus" @mouseup.stop="focus"
                    @keydown="keydown(lines, $event)" @keyup="keyup(lines, $event)" @compositionstart="compositionstart"
                    @compositionend="compositionend">
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
                <div v-else-if="line.tag === 'divider'" :id="line.id" class="divider">
                    <div></div>
                </div>
            </div>
        </div>
    </div>
    <Tag :name="['h1', 'h2', 'h3', 'text']" :lines="lines" />
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

.workspace-horizontal .dragable {
    display: flex;
    align-items: center;
    justify-content: space-around;
    width: 80px;
    height: 3rem;
}

.workspace-vertical .dragable>div {
    height: 40px;
    width: 3rem;
    text-align: center;
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
    max-width: 100%;
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

.line>.divider {
    padding: 0.5rem 0;
}

.line>.divider>div {
    border-top: 1px solid var(--border);
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