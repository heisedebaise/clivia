<script setup>
import { ref } from 'vue';
import { url } from '../http';
import { now } from './time';
import { findById } from './line';
import { setTag } from './keydown';
import { getFocusId, getCursor, setCursor } from './cursor';
import { message } from './locale';
import Icon from './Icon.vue';
import { service } from '../http';

const props = defineProps({
    names: Array,
    lines: Array,
    vertical: Boolean,
});

const position = ref({
    left: -1,
    top: -1,
    index: -1,
});
const ai = ref({
    show: '',
    text: [],
    image: [],
    empty: '',
});
const description = ref(null);

const show = (left, top) => {
    position.value = {
        left,
        top,
        index: -1,
    };
    setCursor();
};

const arrow = (e) => {
    if (e.key === 'ArrowDown' && position.value.index < props.names.length - 1) {
        position.value.index++;
    } else if (e.key === 'ArrowUp' && position.value.index > 0) {
        position.value.index--;
    }
};

const select = (e) => {
    let id = getFocusId();
    if (id === null)
        return;

    let line = findById(props.lines, id);
    if (line === null)
        return;

    let name = null;
    if (e && e.target) {
        let node = e.target;
        for (let i = 0; i < 1024; i++) {
            if (node && node.className === 'tag') {
                name = node.dataset.name;

                break;
            }

            node = node.parentElement;
        }
    } else if (position.value.index > -1 && position.value.index < props.names.length) {
        name = props.names[position.value.index];
    }
    if (name != null) {
        if (name === 'ai-text') {
            ai.value.show = 'text';
            setTimeout(() => description.value.focus(), 100);
        } else if (name === 'ai-image') {
            ai.value.show = 'image';
            setTimeout(() => description.value.focus(), 100);
        } else {
            let cursor = getCursor();
            if (cursor[1] > 0 && cursor[0] < line.texts.length && cursor[1] <= line.texts[cursor[0]].text.length && line.texts[cursor[0]].text.charAt(cursor[1] - 1) === '/') {
                cursor[1]--;
                cursor[3]--;
                line.texts[cursor[0]].text = line.texts[cursor[0]].text.substring(0, cursor[1]) + line.texts[cursor[0]].text.substring(cursor[1] + 1);
            }
            line.tag = name;
            line.time = now();
            setCursor(id, cursor);
        }
    }
    hideTags();
};

const aiGo = (e) => {
    ai.value.text = [];
    ai.value.image = [];
    ai.value.empty = message('ai.wait');
    service('/editor/ai-' + ai.value.show, { content: description.value.innerText, count: e.target.dataset.count }, data => {
        if (data === '') {
            ai.value.empty = message('ai.empty');

            return;
        }

        ai.value.empty = '';
        let array = data.split('\n');
        if (ai.value.show === 'text') {
            ai.value.text = array;
        } else if (ai.value.show === 'image') {
            ai.value.image = array;
        }
    })
};

const hideTags = (e) => {
    position.value = {
        left: -1,
        top: -1,
    };
    setCursor();
    setTag(null);
};

const hideAi = (e) => {
    ai.value.show = '';
};

defineExpose({
    show,
    arrow,
    select,
});
</script>

<template>
    <div v-if="position.left > 0" class="tags-mask" @click="hideTags">
        <div :class="'tags' + (vertical && names.length > 3 ? ' tags-vertical' : ' tags-horizontal')"
            :style="{ left: position.left + 'px', top: position.top + 'px' }">
            <div v-for="(name, index) in names" :class="'tag' + (index === position.index ? ' tag-hover' : '')"
                :data-name="name" @click="select">
                <div class="image">
                    <Icon :name="name" />
                </div>
                <div class="title-sub">
                    <div class="title">{{ message(`tag.${name}`) }}</div>
                    <div class="sub">{{ message(`tag.${name}.sub`) }}</div>
                </div>
            </div>
        </div>
    </div>
    <div v-if="ai.show" class="ai-mask" @click="hideAi">
        <div class="ai" @click.stop="">
            <div class="description">
                <div ref="description" class="input" contenteditable="true"></div>
                <div v-if="ai.show === 'text'" class="go" @click="aiGo">{{ message('ai.go') }}</div>
                <div v-if="ai.show === 'image'" class="go" data-count="1" @click="aiGo">{{ message('ai.go.1') }}</div>
                <div v-if="ai.show === 'image'" class="go" data-count="2" @click="aiGo">{{ message('ai.go.2') }}</div>
                <div v-if="ai.show === 'image'" class="go" data-count="4" @click="aiGo">{{ message('ai.go.4') }}</div>
            </div>
            <div v-if="ai.text.length > 0" class="text">
                <p v-for="text in ai.text">{{ text }}</p>
            </div>
            <div v-if="ai.image.length > 0" class="image">
                <img v-for="image in ai.image" :src="url(image)" />
            </div>
            <div v-if="ai.empty" class="empty">{{ ai.empty }}</div>
        </div>
    </div>
</template>

<style>
.tags-mask,
.ai-mask {
    position: absolute;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
    z-index: 8;
}

.tags {
    position: absolute;
    background-color: var(--background);
    border: 1px solid var(--border);
    border-radius: 4px;
    overflow: hidden;
    width: 320px;
}

.tags-vertical {
    transform: translateX(-100%);
}

.tags .tag {
    display: flex;
    align-items: center;
    padding: 4px 12px;
    cursor: pointer;
}

.tags .tag:hover,
.tags .tag-hover {
    background-color: var(--icon-hover-bg);
    cursor: pointer;
}

.tags .tag img {
    display: block;
    width: 64px;
}

.tags .tag .title-sub {
    padding-left: 8px;
}

.tags .tag .title {
    font-size: 1.25rem;
    color: var(--tag-title);
}

.tags .tag .sub {
    color: var(--tag-sub);
}

.ai {
    position: fixed;
    left: 50vw;
    top: 20vh;
    background-color: var(--background);
    border: 1px solid var(--border);
    border-radius: 4px;
    overflow: hidden;
    transform: translateX(-50%);
}

.ai .description {
    display: flex;
    align-items: stretch;
}

.ai .description .input {
    width: 50vw;
    border: none;
    outline: none;
    padding: 8px;
}

.ai .description .go {
    display: flex;
    align-items: center;
    padding: 0 8px;
    cursor: pointer;
    border-left: 1px solid var(--border);
}

.ai .text,
.ai .image,
.ai .empty {
    max-height: 50vh;
    overflow: auto;
    border-top: 1px solid var(--border);
}

.ai .text {
    padding: 8px 16px;
}

.ai .text p {
    margin: 0;
    padding: 0;
    line-height: 150%;
}

.ai .image {
    padding: 0 8px;
    text-align: center;
}

.ai .image img {
    display: block;
    width: 100%;
    margin: 8px 0;
}

.ai .empty {
    padding: 8px;
    text-align: center;
    color: var(--empty);
}
</style>