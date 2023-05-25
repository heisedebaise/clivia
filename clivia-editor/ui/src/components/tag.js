import { now } from './time';
import { newId } from "./generator";
import { message } from './locale';
import { annotation } from './handler';
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

const newImage = (lines, path, name) => {
    let id = getFocusId();
    if (id === null)
        return;

    let index = findIndex(lines, id);
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
    if (isEmpty(lines[index].texts))
        lines.splice(index, 1, image);
    else
        lines.splice(index + 1, 0, image);
    if (lines[lines.length - 1].tag === 'image')
        lines.push(newText());
    annotation();
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
    if (lines[lines.length - 1].tag === 'divider')
        lines.push(newText());
    annotation();
};

export {
    newText,
    newImage,
    newDivider,
};