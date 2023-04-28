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
        dragable.height = node.offsetWidth;
        dragable.left = node.offsetLeft - 16;
        dragable.top = 16;
    } else {
        dragable.left = 0;
        dragable.top = node.offsetTop;
        dragable.height = node.offsetHeight;
    }
};

const mousedown = (vertical, draging, e) => {
    let node = findDragingNode(vertical, e);
    if (node === null)
        return;

    data.draging = true;
    data.source = node.children[0].id;
    draging.html = node.innerHTML;
};

const mousemove = (vertical, dragable, draging, e) => {
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

const mouseup = (lines, draging, e) => {
    data.draging = false;
    draging.left = -1;
    let source = findIndex(lines, data.source);
    let target = findIndex(lines, data.target);
    let line = lines[source];
    if (source > target) {
        lines.splice(source, 1);
        lines.splice(target, 0, line);
    } else if (source < target) {
        lines.splice(target, 0, line);
        lines.splice(source, 1);
    }
};

export {
    mouseover,
    mousedown,
    mousemove,
    mouseup
};