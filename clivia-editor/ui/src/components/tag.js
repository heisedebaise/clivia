import { newId } from "./generator";
import { getFocusId } from './cursor';
import { findIndex } from "./line";

const newP = () => {
    return {
        id: newId(),
        tag: 'p',
        className: 'empty',
        texts: [{
            text: '',
        }],
    };
};

const newDivider = (lines) => {
    let id = getFocusId();
    if (id === null)
        return;

    lines.splice(findIndex(lines, id) + 1, 0, {
        id: newId(),
        tag: 'divider',
    });
};

export {
    newP,
    newDivider,
};