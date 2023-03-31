const findId = (e) => {
    let node = e.target;
    for (let i = 0; i < 1024; i++) {
        if (node.id)
            return node.id;

        node = node.parentElement;
    }

    return node;
};

export {
    findId
};