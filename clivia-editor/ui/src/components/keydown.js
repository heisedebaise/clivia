import { store } from "../store";
import { isComposition } from "./composition";
import { newId } from "./generator";
import { newText } from "./tag";
import { getCursor, getCursorSingle, setCursor, setCursorSingle } from "./cursor";
import { findEventId } from './event';
import { findIndex, splitTexts, isEmpty } from "./line";

const refs = {
    tag: null,
};

const setTag = (tag) => refs.tag = tag;

const keydown = (e) => {
    if (isComposition())
        return;

    let id = findEventId(e);
    let index = findIndex(id);
    let line = store.lines[index];
    if (e.key === 'Enter')
        enter(line, id, index, e);
    else if (e.key === 'Backspace')
        backspace(line, index, e);
    else if (e.key === 'ArrowUp' || e.key === 'ArrowDown')
        arrow(line, index, e);
    else if (e.key === ' ')
        space(e);
    else if (e.ctrlKey && e.key === 'v')
        e.preventDefault();
};

const enter = (line, id, index, e) => {
    e.preventDefault();
    if (refs.tag != null) {
        refs.tag.select();

        return;
    }

    let cursor = getCursor();
    let texts = splitTexts(line.texts, cursor);
    if (isEmpty(texts[0])) {
        let l = { ...line };
        l.id = newId();
        l.texts = [{
            text: '',
        }];
        store.lines.splice(index, 0, l);
        setCursor(id, [0, 0, 0, 0]);
    } else if (isEmpty(texts[2])) {
        let text = newText();
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

const backspace = (line, index, e) => {
    if (line.texts.length === 1 && line.texts[0].text.length <= 1) {
        e.preventDefault();
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

const arrow = (line, index, e) => {
    e.preventDefault();
    if (refs.tag != null)
        return;

    let i = 0;
    if (e.key === 'ArrowDown' && index < store.lines.length - 1)
        i = 1;
    else if (e.key === 'ArrowUp' && index > 0)
        i = -1;
    if (i != 0)
        setCursorSingle(store.lines[index + i], getCursorSingle(line));
};

const space = (e) => {
    if (refs.tag != null) {
        e.preventDefault();
        refs.tag.select();

        return;
    }
};

export {
    setTag,
    keydown
};