import { newId } from "./generator";
import { now } from './time';
import { message } from './locale';
import { getFocusId } from './cursor';
import { findIndex } from "./line";

const newText = () => {
    return {
        id: newId(),
        tag: 'text',
        placeholder: message('placeholder.text'),
        className: 'empty',
        texts: [{
            text: '',
        }],
        time: now(),
    };
};

const newImage = (lines) => {
    let id = getFocusId();
    if (id === null)
        return;

    lines.splice(findIndex(lines, id) + 1, 0, {
        id: newId(),
        tag: 'image',
        upload: message('image.upload'),
        time: now(),
    });
};

const newDivider = (lines) => {
    let id = getFocusId();
    if (id === null)
        return;

    lines.splice(findIndex(lines, id) + 1, 0, {
        id: newId(),
        tag: 'divider',
        time: now(),
    });
};

export {
    newText,
    newImage,
    newDivider,
};