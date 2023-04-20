import { newId } from "./generator";
import { now } from './time';
import { message } from './locale';
import { getFocusId } from './cursor';
import { findIndex, isEmpty } from "./line";

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

const newImage = (lines, annotation) => {
    let id = getFocusId();
    if (id === null)
        return;

    let index = findIndex(lines, id);
    let image = {
        id: newId(),
        tag: 'image',
        upload: message('image.upload'),
        time: now(),
    };
    if (isEmpty(lines[index].texts))
        lines.splice(index, 1, image);
    else
        lines.splice(index + 1, 0, image);
    if (lines[lines.length - 1].tag === 'image')
        lines.push(newText());
    annotation();
};

const newDivider = (lines, annotation) => {
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