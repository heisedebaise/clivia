import { newId } from "./generator";
import { newText } from "./tag";
import { getCursor, getCursorSingle, setCursor, setCursorSingle } from "./cursor";
import { findEventId } from './event';
import { findIndex, splitTexts, isEmpty } from "./line";

const keydown = (lines, e) => {
    let id = findEventId(e);
    let index = findIndex(lines, id);
    let line = lines[index];
    if (e.key === 'Enter')
        enter(lines, line, id, index, e);
    else if (e.key === 'Backspace')
        backspace(lines, line, id, index, e);
    else if (e.key === 'ArrowUp' || e.key === 'ArrowDown')
        arrow(lines, line, id, index, e);
    else if (e.ctrlKey && e.key === 'v')
        e.preventDefault();
    console.log(e);
};

const enter = (lines, line, id, index, e) => {
    e.preventDefault();
    let cursor = getCursor();
    let texts = splitTexts(line.texts, cursor);
    if (isEmpty(texts[0])) {
        let l = { ...line };
        l.id = newId();
        l.texts = [{
            text: '',
        }];
        lines.splice(index, 0, l);
        setCursor(id, [0, 0, 0, 0]);
    } else if (isEmpty(texts[2])) {
        let text = newText();
        lines.splice(index + 1, 0, text);
        setCursor(text.id, [0, 0, 0, 0]);
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

const backspace = (lines, line, id, index, e) => {
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

const arrow = (lines, line, id, index, e) => {
    e.preventDefault();
    let i = 0;
    if (e.key === 'ArrowDown' && index < lines.length - 1)
        i = 1;
    else if (e.key === 'ArrowUp' && index > 0)
        i = -1;
    if (i != 0)
        setCursorSingle(lines[index + i], getCursorSingle(line));
};

export {
    keydown
};