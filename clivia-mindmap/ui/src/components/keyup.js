import { store } from "../store";
import { now } from './time';
import { isComposition } from './composition';
import { getCursor, setCursor } from "./cursor";
import { trigger } from "./event";

const keyup = () => {
    if (isComposition())
        return;

    let node = store.node();
    if (!node)
        return;

    let text = document.querySelector('#' + store.focus).lastChild.innerText;
    if (node === text)
        return;

    let cursor = getCursor();
    node.text = text;
    node.time = now();
    setCursor(null, cursor);
    trigger('link');
};

export {
    keyup,
};