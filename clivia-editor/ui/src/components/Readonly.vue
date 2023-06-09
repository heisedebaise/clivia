<script setup>
import { ref, onMounted } from 'vue';
import { store } from '../store';
import { url } from '../http';
import Icon from './Icon.vue';

const readonly = ref(null);
const annotations = ref([]);

const annotation = () => {
    setTimeout(() => {
        let index = 0;
        let scrollTop = readonly.value.scrollTop;
        let scrollLeft = readonly.value.scrollLeft;
        for (let line of store.lines) {
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

const direction = () => {
    store.vertical = !store.vertical;
    annotation();
    if (store.vertical)
        setTimeout(() => readonly.value.scrollLeft = readonly.value.scrollWidth, 10);
};

onMounted(annotation);
</script>

<template>
    <div ref="readonly" class="readonly" @scroll="annotation">
        <div :class="'lines lines-' + (store.vertical ? 'vertical' : 'horizontal')">
            <div v-for="line in store.lines" class="line">
                <h1 v-if="line.tag === 'h1'" :id="line.id" :class="line.className">
                    <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
                </h1>
                <h2 v-else-if="line.tag === 'h2'" :id="line.id" :class="line.className">
                    <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
                </h2>
                <h3 v-else-if="line.tag === 'h3'" :id="line.id" :class="line.className">
                    <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
                </h3>
                <p v-else-if="line.tag === 'text'" :id="line.id" :class="line.className">
                    <span v-for="(text, index) in line.texts" :class="text.style" :data-index="index">{{ text.text }}</span>
                </p>
                <div v-else-if="line.tag === 'image' && line.path" :id="line.id" class="image">
                    <div class="view">
                        <img :src="url(line.path)" draggable="false" />
                        <div class="name">{{ line.name }}</div>
                    </div>
                </div>
                <div v-else-if="line.tag === 'divider'" :id="line.id" class="divider">
                    <div></div>
                </div>
            </div>
        </div>
    </div>
    <div v-for="annotation in annotations" :class="'annotation-' + (store.vertical ? 'vertical' : 'horizontal')"
        :style="{ left: annotation.left + 'px', top: annotation.top + 'px' }">{{ annotation.text }}</div>
    <div :class="'direction direction-' + (store.vertical ? 'vertical' : 'horizontal')" @click="direction">
        <Icon name="direction" :enable="true" />
    </div>
</template>

<style>
.readonly {
    width: 100%;
    height: 100%;
    overflow: auto;
}

.readonly .lines-vertical {
    min-width: calc(100% - 16px);
    min-height: calc(100% - 16px);
}

.direction {
    position: fixed;
    right: 16px;
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: var(--background);
    border-radius: 40px;
    cursor: pointer;
    border: 1px solid var(--border);
}

.direction:hover {
    background-color: var(--hover-bg);
}

.direction-horizontal {
    top: 16px;
}

.direction-vertical {
    bottom: 16px;
}
</style>