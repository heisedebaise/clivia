import { nextTick } from "vue";
import { store } from '../store';

const focus = (id) => {
    store.focus = id;
    nextTick(() => document.querySelector('#' + id).focus());
};

export {
    focus,
};