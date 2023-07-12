import { store } from "../store";
import { focus } from "./cursor";
import { trigger } from "./event";

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

const removeNode = (node) => {
    let parent = store.nodes[node.parent];
    if (!parent)
        return;

    let index = parent.children.indexOf(node.id);
    if (index === -1)
        return;

    parent.children.splice(index, 1);
    focus(parent.id);
    remove(node);
    trigger('branch', { type: 'remove', id: parent.id });
};

const remove = (node) => {
    delete store.nodes[node.id];
    if (!node.children || node.children.length === 0)
        return;

    for (let child of node.children)
        remove(store.nodes[child]);
};

const setIndex = (id) => setNodeIndex(store.nodes[id]);

const setNodeIndex = (node) => {
    if (!node || !node.children || node.children.length === 0)
        return;

    for (let i = 0; i < node.children.length; i++) {
        let child = store.nodes[node.children[i]];
        if (!child)
            continue;

        let index = i + 1;
        child.index = node.main ? index : (node.index + '-' + index);
        setNodeIndex(child);
    }
};

export {
    newNode,
    removeNode,
    setIndex,
}