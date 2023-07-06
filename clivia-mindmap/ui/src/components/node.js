import { store } from "../store";

const newNode = (parent) => {
    let node = {
        id: newId(),
        parent: parent,
        text: '',
    };
    store.nodes[node.id] = node;

    return node;
};

const newId = () => {
    for (let i = 0; i < 1024; i++) {
        let id = random('id', 16);
        if (!document.querySelector('#' + id))
            return id;
    }

    return random(16);
};

const random = (prefix, length) => {
    let string = prefix;
    while (string.length < length)
        string += Math.random().toString(36).substring(2);

    return string.substring(0, length);
};

export {
    newNode,
}