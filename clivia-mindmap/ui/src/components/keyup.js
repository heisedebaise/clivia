import { store } from "../store";
import { now } from './time';
import { isComposition } from './composition';

const keyup = () => {
    if (isComposition())
        return;

    let node = store.node();
    if (!node)
        return;

    node.text = document.querySelector('#' + store.focus).innerText;
    node.time = now();
};

export {
    keyup,
};