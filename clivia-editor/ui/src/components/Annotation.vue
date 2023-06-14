<script setup>
import { ref } from 'vue';
import { now } from './time';
import { message } from './locale';
import { annotation } from './annotation';
import { getFocusId, getCursor, setCursor } from './cursor';
import { findById, splitTexts, isEmpty } from './line';
import Icon from './Icon.vue';

const emits = defineEmits(['unset']);

const position = ref({
    left: -1,
    top: -1,
    transform: '',
});

const input = ref('');

const show = (left, top, value, transform) => {
    position.value = { left, top, transform };
    input.value = value || '';
    setCursor();
};

const set = (e) => {
    let id = getFocusId();
    if (id === null)
        return;

    let line = findById(id);
    if (line == null)
        return;

    let texts = splitTexts(line.texts, getCursor());
    if (isEmpty(texts[1]))
        return;

    for (let text of texts[1]) {
        text.annotation = input.value;
        texts[0].push(text);
    }
    for (let text of texts[2])
        texts[0].push(text);
    line.texts = texts[0];
    line.time = now();
    hide();
    setCursor();
    annotation();
}

const unset = (e) => {
    hide();
    emits('unset');
};

const hide = (e) => {
    position.value = {
        left: -1,
        top: -1
    };
    input.value = '';
    setCursor();
};

defineExpose({
    show
})
</script>

<template>
    <div v-if="position.left > 0" class="annotation-mark" @click="hide">
        <div :class="'annotation annotation-' + position.transform"
            :style="{ left: position.left + 'px', top: position.top + 'px' }" @click.stop="">
            <div class="input">
                <input :placeholder="message('placeholder.annotation')" v-model="input" />
            </div>
            <div class="button" @click="set">
                <Icon name="check-circle" :enable="true" />
            </div>
            <div v-if="position.transform === 'y'" class="button" @click="unset">
                <Icon name="close-circle" :enable="true" />
            </div>
        </div>
    </div>
</template>

<style scoped>
.annotation-mark {
    position: absolute;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
}

.annotation {
    position: absolute;
    background-color: var(--background);
    border: 1px solid var(--border);
    border-radius: 4px;
    display: flex;
    align-items: center;
}

.annotation-x {
    transform: translateX(-40%);
}

.annotation-y {
    transform: translateY(-100%);
}

.annotation input {
    border: none;
    outline: none;
}

.annotation .button {
    padding: 4px;
    cursor: pointer;
    border-left: 1px solid var(--border);
}

.annotation .button:hover {
    background-color: var(--hover-bg);
}
</style>