import { store } from '../store';
import { focus } from './cursor';
import { isComposition } from './composition';
import { newNode, removeNode, setIndex, branch } from './node';
import { trigger } from './event';


const keydown = (event) => {
    if (isComposition())
        return;

    let node = store.node();
    if (!node)
        return;

    if (event.key === 'Enter')
        enter(event, node);
    else if (event.key === 'Insert' || event.key === 'Tab')
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
    setIndex(node.parent);
    focus(next.id);
    branch();
};

const insert = (event, node) => {
    event.preventDefault();
    if (!node.children)
        node.children = [];
    let child = newNode(node.id);
    node.children.push(child.id);
    setIndex(node.id);
    focus(child.id);
    branch();
};

const backspace = (node) => {
    if (!node.main && node.text.length === 0)
        removeNode(node);
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