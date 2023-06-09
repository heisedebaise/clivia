import { store } from '../store';
import { now } from './time';
import { newId } from "./generator";
import { message } from './locale';
import { annotation } from './annotation';
import { getFocusId } from './cursor';
import { findIndex, isEmpty } from "./line";

const newText = (text) => {
    return {
        id: newId(),
        tag: 'text',
        texts: [{
            text: text || '',
        }],
        time: now(),
    };
};

const newImage = (path, name) => {
    let id = getFocusId();
    if (id === null)
        return;

    let index = findIndex(id);
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
    annotation();
};

const newDivider = () => {
    let id = getFocusId();
    if (id === null)
        return;

    store.lines.splice(findIndex(id) + 1, 0, {
        id: newId(),
        tag: 'divider',
        time: now(),
    });
    if (store.lines[store.lines.length - 1].tag === 'divider')
        store.lines.push(newText());
    annotation();
};

export {
    newText,
    newImage,
    newDivider,
};