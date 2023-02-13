<script setup>
import { ref, onMounted } from 'vue';
import { service } from './http';
import { message } from './locale';
import { timeout } from './common';
import { formatText, mergeText, clearBr } from './text';
import { getRange, setRange, setCursor } from './selection';
import { bold, italic, underline, through } from './style';
import { keymap } from './keymap';
import { markdown } from './markdown';

const edit = ref({
    enable: true,
});
const focus = ref({
    id: '',
    actions: -1,
    plus: false,
    tags: -1,
    tag: -1,
    select: {
        start: 0,
        end: 0
    },
    range: null,
    style: {
        left: -1,
        top: -1
    },
});
const drag = ref({
    ing: false,
    id: '',
    top: -1,
    offset: 0,
    index: 0,
    last: false,
});
const tags = ref([]);
const list = ref([]);

const randomId = () => {
    let id = 'id';
    while (id.length < 16) id += Math.random().toString(36).substring(2);
    id = id.substring(0, 16);

    return id;
};

const findIndex = (id) => {
    for (let i = 0; i < list.value.length; i++)
        if (list.value[i].id === id)
            return i;

    return -1;
};

const findById = (id) => {
    for (let item of list.value)
        if (item.id === id)
            return item;

    return null;
};

const isEmpty = (item, id, index) => {
    if (!item && id)
        item = findById(id);
    if (!item && index > -1)
        item = list.value[index];

    return item.text.length === 0 || (item.text.length === 1 && item.text[0].text.length === 0);
};

const focusById = (id, delay) => {
    if (delay) {
        setTimeout(() => {
            document.querySelector('#' + id).focus();
            cursor(id);
        }, timeout.min);
    } else {
        document.querySelector('#' + id).focus();
        cursor(id);
    }
};

const findItemNode = (e) => {
    let node = e.target;
    while (node.className != 'item')
        node = node.parentNode;

    return node;
}

const cursor = (id) => {
    focus.value.style.left = -1;
    let node = document.querySelector('#' + (id || focus.value.id));
    if (node.childNodes.length === 0)
        return;

    setTimeout(() => {
        node = node.firstChild;
        let range = document.createRange();
        range.setStart(node, focus.value.select.start);
        range.setEnd(node, focus.value.select.start);
        getSelection().removeAllRanges();
        getSelection().addRange(range);
        focus.value.select.start = 0;
        focus.value.select.end = 0;
    }, timeout.min);
};

const onMouseover = (e) => {
    focus.value.actions = findItemNode(e).offsetTop;
};

const onFocus = (e) => {
    focus.value.id = e.target.id;
};

const onKeyDown = (e) => {
    let index = findIndex(e.target.id);
    let range = getSelection().getRangeAt(0);
    let start = parseInt(range.startContainer.parentNode.dataset.index);
    let end = parseInt(range.endContainer.parentNode.dataset.index);
    if (e.key === 'Enter') {
        e.preventDefault();
        if (focus.value.tags > -1 && focus.value.tag > -1 && focus.value.tag < tags.value.length) {
            focus.value.tags = -1;
            changeTag(index, tags.value[focus.value.tag].name);

            return;
        }

        if (isEmpty(null, null, index)) {
            let id = randomId();
            list.value.splice(index + 1, 0, {
                id: id,
                tag: 'text',
                text: [{ text: '' }],
                placeholder: message('placeholder')
            });
            setTimeout(() => {
                let node = document.querySelector('#' + id).firstElementChild;
                node = node.firstChild || node;
                setRange(node, 0, node, 0);
            }, timeout.min);

            return;
        }

        let text = list.value[index].text;
        let startText = [];
        for (let i = 0; i <= start; i++) {
            let t = text[i];
            startText.push({ ...t });
        }
        startText[start].text = startText[start].text.substring(0, range.startOffset);
        if (startText[start].text === '')
            startText.slice(start, 1);
        list.value[index].text = startText;

        let endText = [];
        for (let i = end; i < text.length; i++) {
            let t = text[i];
            endText.push({ ...t });
        }
        endText[0].text = endText[0].text.substring(range.endOffset);
        if (endText[0].text === '')
            endText.slice(0, 1);

        let id = randomId();
        list.value.splice(index + 1, 0, {
            id: id,
            tag: 'text',
            text: endText,
            placeholder: message('placeholder')
        });
        setTimeout(() => {
            let node = document.querySelector('#' + id).firstElementChild;
            node = node.firstChild || node;
            setRange(node, 0, node, 0);
        }, timeout.min);

        return;
    }

    if (e.key === 'Backspace' && index > 0) {
        if (isEmpty(null, null, index)) {
            e.preventDefault();
            list.value.splice(index, 1);
            setTimeout(() => {
                let item = list.value[index - 1];
                let offset = 0;
                if (item.text.length > 0)
                    offset = item.text[item.text.length - 1].text.length;
                let node = document.querySelector('#' + item.id);
                if (node.lastElementChild)
                    node = node.lastElementChild;
                if (node.firstChild)
                    node = node.firstChild;
                setRange(node, offset, node, offset);
            }, timeout.min);
            setTimeout(() => clearBr(list.value), timeout.larger);
        } else if (start === end && start === 0 && range.endOffset === 0) {
            e.preventDefault();
            let item = list.value[index - 1];
            let text = 0;
            let offset = 0;
            if (item.text.length > 0) {
                text = item.text.length - 1;
                offset = item.text[text].text.length;
            }
            for (let text of list.value[index].text)
                list.value[index - 1].text.push(text);
            list.value.splice(index, 1);
            setTimeout(() => {
                let node = document.querySelector('#' + item.id).childNodes[text + 1].firstChild;
                setRange(node, offset, node, offset);
            }, timeout.larger);
        }

        return;
    }

    if (e.key === 'Delete' && index < list.value.length - 1) {
        if (isEmpty(null, null, index)) {
            e.preventDefault();
            list.value.splice(index, 1);
            setTimeout(() => {
                let item = list.value[index];
                let node = document.querySelector('#' + item.id);
                if (node.firstElementChild)
                    node = node.firstElementChild;
                if (node.firstChild)
                    node = node.firstChild;
                setRange(node, 0, node, 0);
            }, timeout.min);
            setTimeout(() => clearBr(list.value), timeout.larger);
        } else if (isEmpty(null, null, index + 1)) {
            e.preventDefault();
            list.value.splice(index + 1, 1);
            setTimeout(() => clearBr(list.value), timeout.larger);
        } else if (start === end && start === list.value[index].text.length - 1 && range.startOffset === list.value[index].text[start].text.length) {
            e.preventDefault();
            let item = list.value[index];
            let text = 0;
            let offset = 0;
            if (item.text.length > 0) {
                text = item.text.length - 1;
                offset = item.text[text].text.length;
            }
            for (let text of list.value[index + 1].text)
                list.value[index].text.push(text);
            list.value.splice(index + 1, 1);
            setTimeout(() => {
                let node = document.querySelector('#' + item.id).childNodes[text + 1].firstChild;
                setRange(node, offset, node, offset);
            }, timeout.larger);
        }

        return;
    }

    if (e.key === 'ArrowUp') {
        e.preventDefault();
        if (focus.value.tags > -1) {
            focus.value.tag = Math.max(0, focus.value.tag - 1);
        } else if (index > 0) {
            getRange();
            setCursor(list.value, list.value[index - 1].id, true);
        }

        return;
    }

    if (e.key === 'ArrowDown') {
        e.preventDefault();
        if (focus.value.tags > -1) {
            focus.value.tag = Math.min(tags.value.length - 1, focus.value.tag + 1);
        } else if (index < list.value.length - 1) {
            getRange();
            setCursor(list.value, list.value[index + 1].id, true);
        }

        return;
    }

    if (keymap(list.value, e)) {
        e.preventDefault();

        return;
    }

    console.log(e);
};

const onKeyUp = (e) => {
    getRange();
    if (e.key === '/') {
        let item = findItemNode(e);
        focus.value.tags = item.offsetTop + item.clientHeight;
    } else if (e.key === 'Shift') {
        onSelect();
    }

    let item = findById(e.target.id);
    let texts = [];
    formatText(texts, document.querySelector('#' + item.id), 0);
    let empty = isEmpty(item);
    item.text = texts;
    if (!isEmpty(item)) {
        mergeText(item);
        if (markdown(item))
            setCursor(list.value, item.id, true);
        if (empty) {
            setTimeout(() => {
                let node = document.querySelector('#' + item.id).childNodes[1].firstChild;
                setRange(node, texts[0].text.length, node, texts[0].text.length);
            }, timeout.min);
        }
    }
};

const onPlusClick = (e) => {
    let node = findByActions();
    if (!node)
        return;

    node.focus();
    focus.value.plus = true;
    focus.value.tags = node.parentNode.offsetTop + node.parentNode.clientHeight;
};

const onDragStart = (e) => {
    let node = findByActions();
    if (!node)
        return;

    drag.value.ing = true;
    drag.value.id = node.id;
    drag.value.top = focus.value.actions;
    setTimeout(() => {
        let draging = document.querySelector('.draging');
        draging.innerHTML = node.outerHTML;
        drag.value.offset = draging.clientHeight >> 1;
    }, timeout.min);
};

const findByActions = () => {
    for (let item of list.value) {
        let node = document.querySelector('#' + item.id);
        if (node.parentNode.offsetTop === focus.value.actions)
            return node;
    }

    return null;
};

const onDragMove = (e) => {
    if (!drag.value.ing) {
        for (let item of list.value) {
            let node = document.querySelector('#' + item.id).parentNode;
            if (e.pageY >= node.offsetTop && e.pageY <= node.offsetTop + node.clientHeight) {
                focus.value.actions = node.offsetTop;

                return;
            }
        }

        return;
    }

    let y = e.pageY - 16;
    if (y < 0)
        y = 0;
    focus.value.actions = y;

    let node = null;
    for (let i in list.value) {
        let item = list.value[i];
        node = document.querySelector('#' + item.id).parentNode;
        if (e.pageY >= node.offsetTop && e.pageY <= node.offsetTop + node.clientHeight) {
            drag.value.top = node.offsetTop - drag.value.offset;
            drag.value.index = i;
            drag.value.last = false;

            return;
        }
    }
    if (node) {
        drag.value.top = node.offsetTop + node.clientHeight;
        drag.value.last = true;
    }
};

const onDragEnd = (e) => {
    drag.value.ing = false;
    let index = findIndex(drag.value.id);
    if (index === -1)
        return;

    if (drag.value.last) {
        let item = list.value[index];
        list.value.splice(index, 1);
        list.value.push(item);
        focusById(item.id, true);

        return;
    }

    if (index === parseInt(drag.value.index))
        return;

    let item = list.value[index];
    list.value.splice(index, 1);
    list.value.splice(drag.value.index - (index > drag.value.index ? 0 : 1), 0, item);
    focusById(item.id, true);
};

const onTagSelect = (e) => {
    let tag = '';
    for (let node = e.target; true; node = node.parentElement) {
        if (node.className === 'tag') {
            tag = node.dataset.name;

            break;
        }
    }
    if (tag === '')
        return;

    let index = findIndex(focus.value.id);
    if (focus.value.plus) {
        focus.value.id = randomId();
        list.value.splice(index + 1, 0, {
            id: focus.value.id,
            tag,
            text: [],
            placeholder: message('placeholder.' + tag, 'placeholder')
        });
        setTimeout(() => setCursor(list.value, focus.value.id, true), timeout.min);
    } else {
        changeTag(index, tag);
    }
};

const changeTag = (index, tag) => {
    let item = list.value[index];
    let range = getRange(true);
    let text = item.text[range.startIndex].text;
    item.tag = tag;
    item.text[range.startIndex].text = text.substring(0, range.startOffset - 1) + text.substring(range.startOffset);
    item.placeholder = message('placeholder.' + tag, 'placeholder');
    setTimeout(() => setCursor(list.value, item.id, true), timeout.min);
};

const onTagsHide = (e) => {
    focus.value.plus = false;
    focus.value.tags = -1;
};

const onSelect = (e) => {
    let range = getRange();
    if (range.startIndex === range.endIndex && range.startOffset === range.endOffset) {
        focus.value.style.left = -1;

        return;
    }

    let node = document.querySelector('#' + focus.value.id).parentNode;
    focus.value.style.left = node.parentNode.offsetLeft;
    focus.value.style.top = node.offsetTop - 48;
    if (focus.value.style.top < 0) {
        focus.value.style.top = node.offsetTop + node.clientHeight;
    }
};

const onBold = () => {
    bold(list.value);
};

const onItalic = () => {
    italic(list.value);
};

const onUnderline = () => {
    underline(list.value);
};

const onThrough = () => {
    through(list.value);
};

const timer = () => {
};

onMounted(() => {
    service('/editor/get', { key: 'editor' }, data => {
        if (data.length === 0)
            return;

        list.value = data;
        for (let i = 0; i < list.value.length; i++) {
            let item = list.value[i];
            item.time = new Date().getTime();
            item.placeholder = message('placeholder.' + item.tag, 'placeholder');
        }
        for (let tag of ['text', 'h1', 'h2', 'h3']) {
            tags.value.push({
                name: tag,
                title: message('tag.' + tag),
                sub: message('tag.' + tag + '.sub'),
            });
        }
        setInterval(timer, 1000);
    });
});
</script>

<template>
    <div class="drag-drop" @mousemove.stop="onDragMove" @mouseup.stop="onDragEnd"></div>
    <div class="content" @mouseup="onSelect" @dblclick="onSelect">
        <div v-for="item in list" class="item" @mouseover="onMouseover" @keydown="onKeyDown" @keyup="onKeyUp">
            <h1 v-if="item.tag === 'h1'" :id="item.id" :contenteditable="edit.enable"
                :data-placeholder="item.placeholder" @focus="onFocus">
                <span v-for="(text, index) in item.text" :class="text.style" :data-index="index">{{
                    text.text
                }}</span>
            </h1>
            <h2 v-else-if="item.tag === 'h2'" :id="item.id" :contenteditable="edit.enable"
                :data-placeholder="item.placeholder" @focus="onFocus">
                <span v-for="(text, index) in item.text" :class="text.style" :data-index="index">{{
                    text.text
                }}</span>
            </h2>
            <h3 v-else-if="item.tag === 'h3'" :id="item.id" :contenteditable="edit.enable"
                :data-placeholder="item.placeholder" @focus="onFocus">
                <span v-for="(text, index) in item.text" :class="text.style" :data-index="index">{{
                    text.text
                }}</span>
            </h3>
            <p v-else-if="item.tag === 'text'" :id="item.id" :contenteditable="edit.enable"
                :data-placeholder="item.placeholder" @focus="onFocus">
                <span v-for="(text, index) in item.text" :class="text.style" :data-index="index">{{
                    text.text
                }}</span>
            </p>
        </div>
    </div>
    <div v-if="focus.actions > -1" class="actions" :style="{ top: focus.actions + 'px' }">
        <div class="action" @click="onPlusClick">
            <icon name="plus" />
        </div>
        <div class="action" @mousedown="onDragStart" @mousemove.stop="onDragMove" @mouseup.stop="onDragEnd">
            <icon name="drag" />
        </div>
    </div>
    <div v-if="drag.ing" class="draging" :style="{ top: drag.top + 'px' }"></div>
    <div v-if="focus.tags > -1" class="tags-mark" @click="onTagsHide">
        <div class="tags" :style="{ top: focus.tags + 'px' }">
            <div v-for="(tag, index) in tags" :class="'tag' + (index === focus.tag ? ' tag-hover' : '')"
                :data-name="tag.name" @click="onTagSelect">
                <div class="icon">
                    <icon :name="tag.name" />
                </div>
                <div class="title-sub">
                    <div class="title">{{ tag.title }}</div>
                    <div class="sub">{{ tag.sub }}</div>
                </div>
            </div>
        </div>
    </div>
    <div v-if="focus.style.left > 0" class="style"
        :style="{ left: focus.style.left + 'px', top: focus.style.top + 'px' }">
        <div class="bold" @click="onBold">B</div>
        <div class="italic" @click="onItalic">i</div>
        <div class="underline" @click="onUnderline">U</div>
        <div class="through" @click="onThrough">S</div>
    </div>
</template>

<style>
.drag-drop {
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 100px;
}

.content {
    position: absolute;
    left: 100px;
    top: 0;
    right: 0;
    bottom: 0;
    overflow: auto;
    padding: 4px 8px;
}

.content .item>* {
    outline: none;
}

.content .item>*:focus:empty::before {
    content: attr(data-placeholder);
    color: var(--vt-c-text-light-2);
}

@media (prefers-color-scheme: dark) {
    .content .item>*:focus:empty::before {
        color: var(--vt-c-text-dark-2);
    }
}

.content p {
    line-height: 32px;
}

.actions {
    position: absolute;
    right: calc(100vw - 100px);
    display: flex;
    align-items: center;
}

.actions .action {
    display: flex;
    width: 32px;
    height: 32px;
    align-items: center;
    justify-content: center;
}

.actions .action:last-child {
    cursor: grab;
}

.actions .action:hover {
    background-color: #ccc;
}

.draging {
    position: absolute;
    left: 100px;
    right: 0;
    padding: 4px 8px;
    color: #ccc;
    background-color: rgba(200, 200, 200, 0.25);
}

.tags-mark {
    position: absolute;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
}

.tags {
    position: absolute;
    left: 100px;
    background-color: #fff;
    border: 1px solid #ccc;
    border-radius: 4px;
    overflow: hidden;
}

.tags .tag {
    display: flex;
    align-items: center;
    padding: 4px 12px;
    cursor: pointer;
}

.tags .tag:hover,
.tags .tag-hover {
    background-color: #f0f0f0;
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
}

.tags .tag .sub {
    color: #888;
}

.style {
    position: absolute;
    display: flex;
    align-items: center;
    border-radius: 4px;
    box-shadow: rgba(15, 15, 15, 0.05) 0px 0px 0px 1px, rgba(15, 15, 15, 0.1) 0px 3px 6px, rgba(15, 15, 15, 0.2) 0px 9px 24px;
    background-color: #fff;
}

.style div {
    padding: 8px 16px;
    cursor: pointer;
}

.style div:hover {
    background-color: #ccc;
}

.bold {
    font-weight: bold;
}

.italic {
    font-style: italic;
}

.underline {
    text-decoration: underline;
}

.through {
    text-decoration: line-through;
}
</style>
