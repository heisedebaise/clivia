import { store } from "../store";
import { now } from './time';
import { findEventId } from "./event";
import { getCursor, setCursor, getCursorSingle, setCursorSingle } from "./cursor";
import { findIndex, splitTexts, isEmpty } from "./line";
import { newText, newId } from "./tag";
import { isComposition } from "./composition";

const keydown = (event) => {
    if (isComposition())
        return;

    let id = findEventId(event);
    if (id === null)
        return;

    let index = findIndex(id);
    let line = store.lines[index];
    if (event.key === 'Enter')
        enter(event, index, line);
    else if (event.key === 'Backspace')
        backspace(event, index, line);
    else if (event.key === 'ArrowUp' || event.key === 'ArrowDown')
        arrow(event, index, line);
};

const enter = (event, index, line) => {
    event.preventDefault();

    let cursor = getCursor();
    let texts = splitTexts(line.texts, cursor);
    if (isEmpty(texts[0])) {
        let l = { ...line };
        l.id = newId();
        l.texts = [{
            text: '',
        }];
        l.time = now();
        store.lines.splice(index, 0, l);
        setCursor(line.id, [0, 0, 0, 0]);
    } else if (isEmpty(texts[2])) {
        let text = newText('p');
        store.lines.splice(index + 1, 0, text);
        setCursor(text.id, [0, 0, 0, 0]);
    } else {
        let l1 = { ...line };
        l1.texts = texts[0];
        let l2 = { ...line };
        l2.id = newId();
        l2.texts = texts[2];
        store.lines.splice(index, 1, l1);
        store.lines.splice(index + 1, 0, l2);
        setCursor(l2.id, [0, 0, 0, 0]);
    }
};

const backspace = (event, index, line) => {
    if (line.texts.length === 1 && line.texts[0].text.length <= 1) {
        event.preventDefault();
        if (line.texts[0].text.length === 1) {
            line.texts[0].text = '';
        } else if (index > 0) {
            store.lines.splice(index, 1);
            let prev = store.lines[index - 1];
            let container = prev.texts.length - 1;
            let offset = 0;
            for (let text of prev.texts)
                offset += text.text.length;
            setCursor(prev.id, [container, offset, container, offset]);
        }

        return;
    }
};

const arrow = (event, index, line) => {
    event.preventDefault();

    let i = 0;
    if (event.key === 'ArrowDown' && index < store.lines.length - 1)
        i = 1;
    else if (event.key === 'ArrowUp' && index > 0)
        i = -1;
    if (i != 0)
        setCursorSingle(store.lines[index + i], getCursorSingle(line));
};

export {
    keydown,
}