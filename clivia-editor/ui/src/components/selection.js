const getCursor = () => {
    let range = getSelection().getRangeAt(0);

    return [
        getIndex(range.startContainer),
        range.startOffset,
        getIndex(range.endContainer),
        range.endOffset
    ];
};

const getIndex = (container) => parseInt(container.nodeName === '#text' ? container.parentNode.dataset.index : container.dataset.index);

export {
    getCursor
};