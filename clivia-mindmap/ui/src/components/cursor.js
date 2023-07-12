import { nextTick } from "vue";
import { store } from '../store';

const data = {
    cursor: [0, 0]
};

const focus = (id) => {
    store.focus = id;
    nextTick(() => document.querySelector('#' + id).lastChild.focus());
};

const getCursor = () => {
    let selection = getSelection();
    if (selection.rangeCount === 0)
        return null;

    let range = selection.getRangeAt(0);
    data.cursor = [range.startOffset, range.endOffset];

    return data.cursor;
};

const setCursor = (id, cursor) => {
    let node = document.querySelector('#' + (id || store.focus)).lastChild;
    if (!node || !node.childNodes || node.childNodes.length === 0)
        return;

    nextTick(() => {
        let range = document.createRange();
        let container = node.childNodes[0];
        range.setStart(container, cursor[0]);
        range.setEnd(container, cursor[1]);
        let selection = getSelection();
        selection.removeAllRanges();
        selection.addRange(range);
    });
};

export {
    focus,
    getCursor,
    setCursor,
};