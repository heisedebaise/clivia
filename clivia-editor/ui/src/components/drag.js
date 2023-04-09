import { findIdNode } from "./event";

const mouseover = (lines, vertical, dragable, e) => {
    if (vertical) {
        let node = findIdNode(e);
        dragable.left = node.offsetLeft;
        dragable.top = 0;

    } else {
        let node = findIdNode(e);
        dragable.left = 0;
        dragable.top = node.offsetTop;
    }
};

const mousemove = (e) => {
    console.log(e);
};

export {
    mouseover,
    mousemove
};