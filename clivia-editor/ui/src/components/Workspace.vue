<script setup>
import { ref } from 'vue';
import { focus } from './cursor';
import { compositionstart, compositionend } from './composition';
import { keydown } from './keydown';
import { keyup } from './keyup';

const props = defineProps({
    editable: Boolean,
    lines: Array,
});

const vertical = ref(false);

const toolbar = (action) => {
    if (action === 'direction') {
        vertical.value = !vertical.value;
    }
};

const second = () => {
};

const stop = (e) => { };

defineExpose({
    toolbar,
    second,
});
</script>

<template>
    <div :class="'workspace workspace-' + (vertical ? 'vertical' : 'horizontal')">
        <div v-for="line in lines" class="line">
            <h1 v-if="line.tag === 'h1'" :id="line.id" :contenteditable="editable" :class="line.className"
                :data-placeholder="line.placeholder" @focus.stop="focus" @mouseup.stop="focus"
                @keydown="keydown(lines, $event)" @keyup="keyup(lines, $event)" @compositionstart="compositionstart"
                @compositionend="compositionend">
                <span v-for="(text, index) in line.texts" :data-index="index" :class="text.style">{{ text.text }}</span>
            </h1>
            <h2 v-else-if="line.tag === 'h2'" :id="line.id" :contenteditable="editable" :class="line.className"
                :data-placeholder="line.placeholder" @focus.stop="focus" @mouseup.stop="focus"
                @keydown="keydown(lines, $event)" @keyup="keyup(lines, $event)" @compositionstart="compositionstart"
                @compositionend="compositionend">
                <span v-for="(text, index) in line.texts" :data-index="index" :class="text.style">{{ text.text }}</span>
            </h2>
            <h3 v-else-if="line.tag === 'h3'" :id="line.id" :contenteditable="editable" :class="line.className"
                :data-placeholder="line.placeholder" @focus.stop="focus" @mouseup.stop="focus"
                @keydown="keydown(lines, $event)" @keyup="keyup(lines, $event)" @compositionstart="compositionstart"
                @compositionend="compositionend">
                <span v-for="(text, index) in line.texts" :data-index="index" :class="text.style">{{ text.text }}</span>
            </h3>
            <p v-else-if="line.tag === 'p'" :id="line.id" :contenteditable="editable" :class="line.className"
                :data-placeholder="line.placeholder" @focus.stop="focus" @mouseup.stop="focus"
                @keydown="keydown(lines, $event)" @keyup="keyup(lines, $event)" @compositionstart="compositionstart"
                @compositionend="compositionend">
                <span v-for="(text, index) in line.texts" :data-index="index" :class="text.style">{{ text.text }}</span>
            </p>
            <div v-else-if="line.tag === 'divider'" :id="line.id" class="divider">
                <div></div>
            </div>
        </div>
    </div>
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

.line>p {
    min-height: 3rem;
    line-height: 3rem;
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
}</style>