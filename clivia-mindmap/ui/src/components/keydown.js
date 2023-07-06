import { store } from '../store';
import { focus } from './cursor';
import { isComposition } from './composition';
import { newNode } from './node';

const keydown = (event) => {
    if (isComposition())
        return;

    let node = store.node();
    if (!node)
        return;

    if (event.key === 'Enter')
        enter(event, node);
    else if (event.key === 'Insert')
        insert(event, node);
    else if (event.key === 'Backspace' || event.key === 'Delete')
        backspace(node);
    else if (event.key === 'ArrowUp' || event.key === 'ArrowDown')
        arrowUpDown(event, node);
    else if (event.key === 'ArrowLeft' || event.key === 'ArrowRight')
        arrowLeftRight(event, node);
};

const enter = (event, node) => {
    event.preventDefault();
    if (node.main)
        return;

    let parent = store.nodes[node.parent];
    if (!parent || !parent.children)
        return;

    let index = parent.children.indexOf(node.id);
    if (index === -1)
        return;

    let next = newNode(node.parent);
    parent.children.splice(index + 1, 0, next.id);
    focus(next.id);
};

const insert = (event, node) => {
    event.preventDefault();
    if (!node.children)
        node.children = [];
    let child = newNode(node.id);
    node.children.push(child.id);
    focus(child.id);
};

const backspace = (node) => {
    if (node.text.length > 0 || node.main)
        return;

    let parent = store.nodes[node.parent];
    if (!parent)
        return;

    let index = parent.children.indexOf(node.id);
    if (index === -1)
        return;

    parent.children.splice(index, 1);
    focus(parent.id);
    remove(node);
};

const remove = (node) => {
    delete store.nodes[node.id];
    if (!node.children || node.children.length === 0)
        return;

    for (let child of node.children)
        remove(store.nodes[child]);
};

const arrowUpDown = (event, node) => {
    event.preventDefault();
    if (node.main)
        return;

    let parent = store.nodes[node.parent];
    if (!parent || !parent.children)
        return;

    let index = parent.children.indexOf(node.id);
    if (index === -1)
        return;

    if (event.key === 'ArrowDown' && index < parent.children.length - 1)
        focus(parent.children[index + 1]);
    else if (event.key === 'ArrowUp' && index > 0)
        focus(parent.children[index - 1]);
};

const arrowLeftRight = (event, node) => {
    let range = getSelection().getRangeAt(0);
    if (!range.collapsed)
        return;

    if (event.key === 'ArrowLeft' && range.startOffset === 0 && node.parent) {
        focus(node.parent);
    } else if (event.key === 'ArrowRight' && range.startOffset === node.text.length && node.children && node.children.length > 0) {
        focus(node.children[0]);
    }
};

export {
    keydown
};