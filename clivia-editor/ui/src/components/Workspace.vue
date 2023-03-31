<script setup>
import { ref } from 'vue';
import { focus } from './focus';
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

const stop = (e) => { };

defineExpose({
    toolbar,
});
</script>

<template>
    <div :class="'workspace workspace-' + (vertical ? 'vertical' : 'horizontal')">
        <div v-for="line in lines" class="line">
            <h1 v-if="line.tag === 'h1'" :id="line.id" @click="focus">
                <span v-for="text in line.texts" :contenteditable="editable" @click.stop="stop">{{ text.text }}</span>
            </h1>
            <h2 v-else-if="line.tag === 'h2'" :id="line.id" @click="focus">
                <span v-for="text in line.texts" :contenteditable="editable" @click.stop="stop">{{ text.text }}</span>
            </h2>
            <h3 v-else-if="line.tag === 'h3'" :id="line.id" @click="focus">
                <span v-for="text in line.texts" :contenteditable="editable" @click.stop="stop">{{ text.text }}</span>
            </h3>
            <p v-else-if="line.tag === 'p'" :id="line.id" @click="focus" @keydown="keydown(lines, $event)"
                @keyup="keyup(lines, $event)" @compositionstart="compositionstart" @compositionend="compositionend">
                <span v-for="(text, index) in line.texts" :contenteditable="editable" :data-index="index"
                    @click.stop="stop">{{ text.text }}</span>
            </p>
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
}

.line>p {
    min-height: 2rem;
    line-height: 2rem;
}

.line span {
    border: none;
    outline: none;
}
</style>