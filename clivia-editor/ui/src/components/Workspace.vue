<script setup>
import { ref, onMounted } from 'vue';
import { service, url } from '../http';
import { setAnnotation } from './handler';
import { findIndex, isEmpty } from './line';
import { focus } from './cursor';
import { setTag, keydown } from './keydown';
import { compositionstart, compositionend, keyup } from './keyup';
import { mouseover, mousedown, mousemove, mouseup } from './drag';
import { newText } from './tag';
import { selectImage, uploadImage, imageName } from './image';
import Icon from './Icon.vue';
import Tag from './Tag.vue';
import { getFocusId } from './cursor';

const props = defineProps({
    lines: Array,
});

const vertical = ref(false);

const toolbar = (action) => {
    if (action === 'direction') {
        vertical.value = !vertical.value;
        dragable.value = {};
        annotation();
    }
};

const workspace = ref(null);
const placeholder = ref({
    id: '',
    text: '',
});
const dragable = ref({});
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

    tag.value.show(workspace.value, node);
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
        <div v-if="dragable.left || dragable.top" class="dragable"
            :style="{ left: dragable.left + 'px', top: dragable.top + 'px', width: dragable.width + 'px', height: dragable.height + 'px' }">
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
        <div :class="'lines lines-' + (vertical ? 'vertical' : 'horizontal')" @mousemove.stop="">
            <div v-for="line in lines" class="line" @mouseover="mouseover(vertical, dragable, $event)">
                <h1 v-if="line.tag === 'h1'" :id="line.id" contenteditable="true"
                    @focus.stop="focus(line, placeholder, $event)" @mouseup.stop="focus(line, placeholder, $event)"
                    @keydown="keydown(lines, $event)" @keyup="keyup(lines, workspace, tag, placeholder, $event)"
                    @compositionstart="compositionstart"
                    @compositionend="compositionend(lines, workspace, tag, placeholder, $event)">
                    <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
                    <span v-if="placeholder.id === line.id" class="placeholder">{{ placeholder.text }}</span>
                </h1>
                <h2 v-else-if="line.tag === 'h2'" :id="line.id" contenteditable="true"
                    @focus.stop="focus(line, placeholder, $event)" @mouseup.stop="focus(line, placeholder, $event)"
                    @keydown="keydown(lines, $event)" @keyup="keyup(lines, workspace, tag, placeholder, $event)"
                    @compositionstart="compositionstart"
                    @compositionend="compositionend(lines, workspace, tag, placeholder, $event)">
                    <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
                    <span v-if="placeholder.id === line.id" class="placeholder">{{ placeholder.text }}</span>
                </h2>
                <h3 v-else-if="line.tag === 'h3'" :id="line.id" contenteditable="true"
                    @focus.stop="focus(line, placeholder, $event)" @mouseup.stop="focus(line, placeholder, $event)"
                    @keydown="keydown(lines, $event)" @keyup="keyup(lines, workspace, tag, placeholder, $event)"
                    @compositionstart="compositionstart"
                    @compositionend="compositionend(lines, workspace, tag, placeholder, $event)">
                    <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
                    <span v-if="placeholder.id === line.id" class="placeholder">{{ placeholder.text }}</span>
                </h3>
                <p v-else-if="line.tag === 'text'" :id="line.id" contenteditable="true"
                    @focus.stop="focus(line, placeholder, $event)" @mouseup.stop="focus(line, placeholder, $event)"
                    @keydown="keydown(lines, $event)" @keyup="keyup(lines, workspace, tag, placeholder, $event)"
                    @compositionstart="compositionstart"
                    @compositionend="compositionend(lines, workspace, tag, placeholder, $event)">
                    <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
                    <span v-if="placeholder.id === line.id" class="placeholder">{{ placeholder.text }}</span>
                </p>
                <div v-else-if="line.tag === 'image'" :id="line.id" class="image">
                    <div v-if="line.uploading" class="uploading">{{ line.uploading }}</div>
                    <div v-else-if="line.path" class="view">
                        <img :src="url(line.path)" draggable="false" />
                        <div class="name" contenteditable="true" @keyup.stop="imageName(lines, $event)">{{ line.name }}
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
</style>