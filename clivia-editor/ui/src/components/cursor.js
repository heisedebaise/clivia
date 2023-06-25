import { nextTick } from "vue";
import { store } from "../store";
import { trigger, findEventId } from "./event";
import { findLine, isEmpty } from "./line";

const data = {
    cursor: [0, 0, 0, 0],
    select: [],
};

const focus = (event) => {
    let id = findEventId(event);
    if (id === null)
        return;

    let line = findLine(id);
    if (line === null)
        return;

    if (isEmpty(line.texts))
        setCursor(id, [0, 0, 0, 0]);
    else {
        getCursorSync();
        store.focus = id;
        trigger('focus');
    }
};

const getCursor = () => data.cursor;

const setCursor = (id, cursor) => {
    nextTick(() => {
        if (id) {
            store.focus = id;
            trigger('focus');
        }
        else
            id = store.focus;
        let node = document.querySelector('#' + id);
        if (!node)
            return;

        if (!cursor)
            cursor = data.cursor;
        if (cursor[0] >= node.children.length)
            cursor[0] = node.children.length - 1;
        if (cursor[2] >= node.children.length)
            cursor[2] = node.children.length - 1;
        if (cursor[0] > cursor[2])
            cursor[0] = cursor[2];
        let start = node.children[cursor[0]];
        if (!start)
            return;

        if (cursor[1] > start.innerHTML.length)
            cursor[1] = start.innerHTML.length;
        let end = node.children[cursor[2]];
        if (!end)
            return;

        if (cursor[3] > end.innerHTML.length)
            cursor[3] = end.innerHTML.length;
        let range = document.createRange();
        range.setStart(start.childNodes.length > 0 ? start.childNodes[0] : start, cursor[1]);
        range.setEnd(end.childNodes.length > 0 ? end.childNodes[0] : end, cursor[3]);
        let selection = getSelection();
        selection.removeAllRanges();
        selection.addRange(range);
    });
};

const getCursorSync = () => {
    let selection = getSelection();
    if (selection.rangeCount === 0)
        return null;

    let range = selection.getRangeAt(0);
    if (range.startContainer.id && range.startContainer.id.indexOf('id') === 0) {
        let offset = range.startContainer.innerText.length;
        data.cursor = [0, offset, 0, offset];
    } else {
        data.cursor = [
            getIndex(range.startContainer),
            range.startOffset,
            getIndex(range.endContainer),
            range.endOffset
        ];
    }
    for (let select of data.select)
        select(!range.collapsed);

    return data.cursor;
};

const getIndex = (container) => parseInt(container.nodeName === '#text' ? container.parentNode.dataset.index : container.dataset.index);

const getCursorSingle = (line) => {
    let cursor = data.cursor;
    let position = 0;
    for (let i = 0; i < cursor[0]; i++)
        position += line.texts[i].text.length;

    return position + cursor[1];
};

const setCursorSingle = (line, cursor) => {
    for (let i = 0; i < line.texts.length; i++) {
        if (cursor <= line.texts[i].text.length) {
            setCursor(line.id, [i, cursor, i, cursor]);

            return;
        }

        cursor -= line.texts[i].text.length;
    }
    let container = line.texts.length - 1;
    let offset = line.texts[container].text.length;
    setCursor(line.id, [container, offset, container, offset]);
};

export {
    focus,
    getCursor,
    setCursor,
    getCursorSync,
    getCursorSingle,
    setCursorSingle,
};