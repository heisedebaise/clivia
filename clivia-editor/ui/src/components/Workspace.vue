<script setup>
import { ref } from 'vue';
import { focus } from './cursor';
import { compositionstart, compositionend } from './composition';
import { keydown } from './keydown';
import { keyup } from './keyup';
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

const annotations = ref([]);

const annotation = () => {
    setTimeout(() => {
        let index = 0;
        let scrollTop = document.querySelector('.workspace').scrollTop;
        let scrollLeft = document.querySelector('.workspace').scrollLeft;
        console.log(screenTop);
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
                    left: node.offsetLeft,
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
    <div :class="'workspace workspace-' + (vertical ? 'vertical' : 'horizontal')" @scroll="scroll">
        <div v-for="line in lines" class="line">
            <h1 v-if="line.tag === 'h1'" :id="line.id" :contenteditable="editable" :class="line.className"
                :placeholder="line.placeholder" @focus.stop="focus" @mouseup.stop="focus" @keydown="keydown(lines, $event)"
                @keyup="keyup(lines, $event)" @compositionstart="compositionstart" @compositionend="compositionend">
                <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
            </h1>
            <h2 v-else-if="line.tag === 'h2'" :id="line.id" :contenteditable="editable" :class="line.className"
                :placeholder="line.placeholder" @focus.stop="focus" @mouseup.stop="focus" @keydown="keydown(lines, $event)"
                @keyup="keyup(lines, $event)" @compositionstart="compositionstart" @compositionend="compositionend">
                <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
            </h2>
            <h3 v-else-if="line.tag === 'h3'" :id="line.id" :contenteditable="editable" :class="line.className"
                :placeholder="line.placeholder" @focus.stop="focus" @mouseup.stop="focus" @keydown="keydown(lines, $event)"
                @keyup="keyup(lines, $event)" @compositionstart="compositionstart" @compositionend="compositionend">
                <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
            </h3>
            <p v-else-if="line.tag === 'text'" :id="line.id" :contenteditable="editable" :class="line.className"
                :placeholder="line.placeholder" @focus.stop="focus" @mouseup.stop="focus" @keydown="keydown(lines, $event)"
                @keyup="keyup(lines, $event)" @compositionstart="compositionstart" @compositionend="compositionend">
                <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
            </p>
            <div v-else-if="line.tag === 'image'" :id="line.id" class="image">
                <div v-if="line.uploading">{{ line.uploading }}</div>
                <div v-else-if="line.path">
                    <img :src="line.url" draggable="false" />
                    <div class="name">{{ line.name }}</div>
                </div>
                <div v-else class="select" @click="selectImage">{{ line.upload }}</div>
            </div>
            <div v-else-if="line.tag === 'divider'" :id="line.id" class="divider">
                <div></div>
            </div>
        </div>
    </div>
    <Tag :name="['h1', 'h2', 'h3', 'text']" :lines="lines" />
    <div v-for="annotation in annotations" :class="'annotation-' + (vertical ? 'vertical' : 'horizontal')"
        :style="{ left: annotation.left + 'px', top: annotation.top + 'px' }">{{ annotation.text }}</div>
</template>

<style>
.workspace {
    position: absolute;
    right: 0;
    bottom: 0;
    overflow: auto;
    padding: 8px;
}

.workspace-horizontal {
    left: 80px;
    top: 42px;
}

.workspace-vertical {
    left: 0;
    top: 122px;
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

.line>.image .select {
    line-height: 250%;
    text-align: center;
    background-color: var(--image-select-bg);
    cursor: pointer;
}

.line>.image .select:hover {
    background-color: var(--image-select-hover-bg);
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