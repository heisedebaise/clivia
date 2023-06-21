const findIdNode = (node) => {
    for (let i = 0; i < 1024; i++) {
        if (node.id && node.id.indexOf('id') === 0)
            return node;

        node = node.parentElement;
    }

    return null;
};

export {
    findIdNode,
}