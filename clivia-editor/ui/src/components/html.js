const clearBr = (lines) => {
    for (let line of lines) {
        let node = document.querySelector('#' + line.id);
        for (let child of node.childNodes) {
            if (child.nodeName === 'BR') {
                node.removeChild(child);
            }
        }
    }
};

export {
    clearBr
};