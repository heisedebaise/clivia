import { findById } from "./line";

const getCursor = () => {
    let range = getSelection().getRangeAt(0);

    return [
        getIndex(range.startContainer),
        range.startOffset,
        getIndex(range.endContainer),
        range.endOffset
    ];
};

const getCursorSingle = (line) => {
    let cursor = getCursor();
    let position = 0;
    for (let i = 0; i < cursor[0]; i++)
        position += line.texts[i].text.length;

    return position + cursor[1];
};

const setCursor = (id,cursor) => {
    setTimeout(() => {
        let node = document.querySelector('#' + id);
        if (!node)
            return;

        if (cursor[0] >= node.children.length)
            cursor[0] = node.children.length - 1;
        if (cursor[2] >= node.children.length)
            cursor[2] = node.children.length - 1;
        if (cursor[0] > cursor[2])
            cursor[0] = cursor[2];
        let start = node.children[cursor[0]];
        if (cursor[1] > start.innerHTML.length)
            cursor[1] = start.innerHTML.length;
        let end = node.children[cursor[2]];
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

const findCursorId = () => {
    let range = getSelection().getRangeAt(0);
    if (range) {
        let node = range.startContainer;
        for (let i = 0; i < 1024; i++) {
            if (node.id && node.id.indexOf('id') === 0)
                return node.id;

            node = node.parentElement;
        }
    }

    return null;
};

const getIndex = (container) => parseInt(container.nodeName === '#text' ? container.parentNode.dataset.index : container.dataset.index);

export {
    getCursor,
    getCursorSingle,
    setCursor,
    setCursorSingle,
    findCursorId,
};