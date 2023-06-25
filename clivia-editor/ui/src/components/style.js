import { store } from '../store';
import { now } from './time';
import { getCursor, setCursor } from "./cursor";
import { findLine, splitTexts, mergeTexts } from "./line";

const setStyleName = (name) => {
    let line = findLine(store.focus);
    if (line === null)
        return;

    let cursor = getCursor();
    if (cursor[0] === cursor[2] && cursor[1] === cursor[3])
        cursor = [0, 0, line.texts.length - 1, line.texts[line.texts.length - 1].text.length];

    style(line, cursor, name);
};

const style = (line, cursor, name) => {
    let texts = splitTexts(line.texts, cursor);
    let start = textLength(texts[0]);
    let end = start + textLength(texts[1]);
    for (let i = 0; i < texts[1].length; i++) {
        if (!texts[1][i].style) {
            texts[1][i].style = name;
        } else if (texts[1][i].style.indexOf(name) > -1) {
            texts[1][i].style = texts[1][i].style.replaceAll(name, '');
        } else if (setColor(texts[1][i], name, 'forground') || setColor(texts[1][i], name, 'background')) {
        } else {
            texts[1][i].style += ' ' + name;
        }
        texts[0].push(texts[1][i]);
    }
    for (let text of texts[2])
        texts[0].push(text);
    mergeTexts(texts[0]);
    line.texts = texts[0];
    line.time = now();

    for (let i = 0; i < line.texts.length; i++) {
        let length = line.texts[i].text.length;
        if (start >= 0 && start < length) {
            cursor[0] = i;
            cursor[1] = start;
        }
        start -= length;

        if (end > 0 && end <= length) {
            cursor[2] = i;
            cursor[3] = end;

            break;
        }
        end -= length;
    }
    setCursor(line.id, cursor);
};

const textLength = (texts) => {
    let length = 0;
    for (let text of texts)
        length += text.text.length;

    return length;
};

const setColor = (text, name, mode) => {
    if (name.indexOf(mode) === -1)
        return false;

    if (text.style.indexOf(mode) === -1) {
        text.style += ' ' + name;
    } else {
        let array = text.style.split(' ');
        for (let i = 0; i < array.length; i++) {
            if (array[i].indexOf(mode) === 0) {
                array[i] = name;

                break;
            }
        }
        text.style = array.join(' ');
    }

    return true;
}

export {
    setStyleName,
    style,
};