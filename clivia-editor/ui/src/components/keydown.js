import { newId } from "./generator";
import { getCursor, setCursor } from "./cursor";
import { findEventId } from './event';
import { findIndex, splitTexts, isEmpty } from "./line";

const keydown = (lines, e) => {
    if (e.key === 'Enter')
        enter(lines, e);
    if (e.key === 'Backspace')
        backspace(lines, e);
    // console.log(e);
};

const enter = (lines, e) => {
    e.preventDefault();
    let cursor = getCursor();
    let id = findEventId(e);
    let index = findIndex(lines, id);
    let line = lines[index];
    let texts = splitTexts(line.texts, cursor);
    if (texts[0].length === 0) {
        let l = { ...line };
        l.id = newId();
        l.texts = [{
            text: '',
        }];
        lines.splice(index, 0, l);
        setCursor(id, [0, 0, 0, 0]);
    } else if (texts[2].length === 0) {
        let p = newP();
        lines.splice(index + 1, 0, p);
        setCursor(p.id, [0, 0, 0, 0]);
    } else {
        let l1 = { ...line };
        l1.texts = texts[0];
        let l2 = { ...line };
        l2.id = newId();
        l2.texts = texts[2];
        lines.splice(index, 1, l1);
        lines.splice(index + 1, 0, l2);
        setCursor(l2.id, [0, 0, 0, 0]);
    }
};

const backspace = (lines, e) => {
    let id = findEventId(e);
    let index = findIndex(lines, id);
    let line = lines[index];
    if (line.texts.length === 1 && line.texts[0].text.length <= 1) {
        e.preventDefault();
        if (line.texts[0].text.length === 1) {
            line.texts[0].text = '';
        } else if (index > 0) {
            lines.splice(index, 1);
            let prev = lines[index - 1];
            let container = prev.texts.length - 1;
            let offset = 0;
            for (let text of prev.texts)
                offset += text.text.length;
            setCursor(prev.id, [container, offset, container, offset]);
        }

        return;
    }
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