import { store } from '../store';
import { findEventId } from "./event";
import { isEmpty } from "./line";
import { message } from "./locale";

const data = {
    focus: null,
    cursor: null,
    select: [],
};

const focus = (index, e) => {
    if (e)
        data.focus = findEventId(e);

    let line = null;
    if (index || index === 0)
        line = store.lines[index];

    if (line && isEmpty(line.texts)) {
        store.placeholder = line.id;
        setCursor(line.id, [0, 0, 0, 0]);
    } else {
        if (line)
            store.placeholder = '';
        setTimeout(getCursorSync, 250);
    }
};

const focusLast = () => {
    document.querySelector('#' + store.lines[store.lines.length - 1].id).focus();
};

const getFocusId = () => data.focus;

const getCursor = () => data.cursor;

const getCursorSync = () => {
    let range = getSelection().getRangeAt(0);
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

const getCursorSingle = (line) => {
    let cursor = getCursor();
    let position = 0;
    for (let i = 0; i < cursor[0]; i++)
        position += line.texts[i].text.length;

    return position + cursor[1];
};

const setCursor = (id, cursor) => {
    setTimeout(() => {
        if (!id)
            id = getFocusId();
        let node = document.querySelector('#' + id);
        if (!node)
            return;

        if (!cursor)
            cursor = getCursor();
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
    }, 10);
};

const setCursorSingle = (line, position) => {
    for (let i = 0; i < line.texts.length; i++) {
        if (position <= line.texts[i].text.length) {
            setCursor(line.id, [i, position, i, position]);

            return;
        }

        position -= line.texts[i].text.length;
    }
    let container = line.texts.length - 1;
    let offset = line.texts[container].text.length;
    setCursor(line.id, [container, offset, container, offset]);
};

const getIndex = (container) => parseInt(container.nodeName === '#text' ? container.parentNode.dataset.index : container.dataset.index);

const bindSelect = (func) => {
    data.select.push(func);
}

export {
    focus,
    focusLast,
    getFocusId,
    getCursor,
    getCursorSync,
    getCursorSingle,
    setCursor,
    setCursorSingle,
    bindSelect,
};