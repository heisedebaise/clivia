import { nextTick } from "vue";
import { store } from "../store";
import { now } from "./time";
import { message } from './locale';
import { trigger } from "./event";
import { findIndex, isEmpty } from "./line";

const newText = (tag, text) => {
    return {
        id: newId(),
        tag: tag || 'p',
        texts: [{
            text: text || '',
        }],
        time: now(),
    };
};

const newImage = (path, name) => {
    let index = findIndex(store.focus);
    let image = {
        id: newId(),
        tag: 'image',
        time: now(),
    };
    if (path)
        image.path = path;
    else
        image.upload = message('image.upload');
    if (name)
        image.name = name;
    if (isEmpty(store.lines[index].texts))
        store.lines.splice(index, 1, image);
    else
        store.lines.splice(index + 1, 0, image);
    if (store.lines[store.lines.length - 1].tag === 'image')
        store.lines.push(newText());
    trigger('annotation');
};

const newDivider = () => {
    let id = newId();
    store.lines.splice(findIndex(store.focus) + 1, 0, {
        id: id,
        tag: 'divider',
        time: now(),
    });
    if (store.lines[store.lines.length - 1].tag === 'divider')
        store.lines.push(newText());
    store.focus = id;
    nextTick(() => trigger('focus'));
    trigger('annotation');
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

export {
    newText,
    newImage,
    newDivider,
    newId,
}