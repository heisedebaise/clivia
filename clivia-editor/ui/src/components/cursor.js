const getCursor = () => {
    let range = getSelection().getRangeAt(0);

    return [
        getIndex(range.startContainer),
        range.startOffset,
        getIndex(range.endContainer),
        range.endOffset
    ];
};

const setCursor = (id, startContainer, startOffset, endContainer, endOffset) => {
    setTimeout(() => {
        let node = document.querySelector('#' + id);
        if (!node)
            return;

        if (startContainer >= node.children.length)
            startContainer = node.children.length - 1;
        if (endContainer >= node.children.length)
            endContainer = node.children.length - 1;
        if (startContainer > endContainer)
            startContainer = endContainer;
        let start = node.children[startContainer];
        if (startOffset > start.innerHTML.length)
            startOffset = start.innerHTML.length;
        let end = node.children[endContainer];
        if (endOffset > end.innerHTML.length)
            endOffset = end.innerHTML.length;
        let range = document.createRange();
        range.setStart(start.childNodes.length > 0 ? start.childNodes[0] : start, startOffset);
        range.setEnd(end.childNodes.length > 0 ? end.childNodes[0] : end, endOffset);
        let selection = getSelection();
        selection.removeAllRanges();
        selection.addRange(range);
    }, 10);
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
    setCursor,
    findCursorId,
};