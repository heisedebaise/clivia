import { store } from '../store';
import { now } from './time';
import { findIdNode } from "./event";
import { findIndex } from "./line";

const data = {
    draging: false,
    source: '',
    target: '',
};

const mouseover = (vertical, dragable, e) => {
    let node = findIdNode(e);
    if (vertical) {
        dragable.left = node.offsetLeft;
        dragable.top = 0;
        dragable.width = node.offsetWidth;
        dragable.height = 80;
    } else {
        dragable.left = 0;
        dragable.top = node.offsetTop;
        dragable.width = 80;
        dragable.height = node.offsetHeight;
    }
};

const dragStart = (vertical, draging, e) => {
    let node = findDragingNode(vertical, e);
    if (node === null)
        return;

    node.children[0].blur();
    data.draging = true;
    data.source = node.children[0].id;
    draging.html = node.innerHTML;
};

const dragMove = (vertical, dragable, draging, e) => {
    if (!data.draging)
        return;

    let node = findDragingNode(vertical, e);
    if (node === null)
        return;

    data.target = node.children[0].id;
    if (vertical) {
        dragable.left = e.x - dragable.height / 2 - 16;
        draging.left = node.offsetLeft + 8;
        draging.top = 122;
    } else {
        dragable.top = e.y - dragable.height / 2 - 42;
        draging.left = 80;
        draging.top = node.offsetTop + 21;
    }
};

const findDragingNode = (vertical, e) => {
    if (e.touches && e.touches.length > 0) {
        e.x = e.touches[0].pageX;
        e.y = e.touches[0].pageY;
    }
    for (let node of document.querySelectorAll('.line')) {
        if (vertical) {
            if (e.x >= node.offsetLeft && e.x < node.offsetLeft + node.offsetWidth)
                return node;
        } else {
            let top = node.offsetTop + 42;
            if (e.y >= top && e.y < top + node.offsetHeight)
                return node;
        }
    }

    return null;
};

const dragDone = (draging, e) => {
    data.draging = false;
    draging.left = -1;
    let source = findIndex(data.source);
    let target = findIndex(data.target);
    let line = store.lines[source];
    line.time = now();
    if (source > target) {
        store.lines.splice(source, 1);
        store.lines.splice(target, 0, line);
    } else if (source < target) {
        store.lines.splice(target, 0, line);
        store.lines.splice(source, 1);
    }
};

export {
    mouseover,
    dragStart,
    dragMove,
    dragDone
};