import { newId } from "./generator";
import { getCursor } from "./selection";
import { findId } from './event';
import { findIndex } from "./line";

const keydown = (lines, e) => {
    if (e.key === 'Enter')
        enter(lines, e);
    // console.log(e);
};

const enter = (lines, e) => {
    e.preventDefault();
    let cursor = getCursor();
    let id = findId(e);
    let index = findIndex(lines, id);
    lines.splice(index + 1, 0, newP());
};

const newP = () => {
    return {
        id: newId(),
        tag: 'p',
        texts: [{
            text: '',
        }],
    };
};

export {
    keydown
};