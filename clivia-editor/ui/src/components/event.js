const findEventId = (e) => {
    let node = findIdNode(e);

    return node == null ? null : node.id;
};

const findIdNode = (e) => {
    let node = e.target;
    for (let i = 0; i < 1024; i++) {
        if (node.id && node.id.indexOf('id') === 0)
            return node;

        node = node.parentElement;
    }

    return null;
};

export {
    findEventId,
    findIdNode,
};