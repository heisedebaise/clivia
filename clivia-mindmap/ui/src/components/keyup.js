import { store } from "../store";
import { now } from './time';
import { isComposition } from './composition';
import { getCursor, setCursor } from "./cursor";

const keyup = () => {
    if (isComposition())
        return;

    let node = store.node();
    if (!node)
        return;

    let cursor = getCursor();
    node.text = document.querySelector('#' + store.focus).innerText;
    node.time = now();
    setCursor(null, cursor);
};

export {
    keyup,
};