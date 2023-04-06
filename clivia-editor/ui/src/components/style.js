import { getFocusId, getCursor, setCursor } from "./cursor";
import { findById, splitTexts, mergeTexts } from "./line";

const bold = (lines) => style(lines, 'bold');

const italic = (lines) => style(lines, 'italic');

const underline = (lines) => style(lines, 'underline');

const linethrough = (lines) => style(lines, 'linethrough');

const style = (lines, name) => {
    let id = getFocusId();
    if (id === null)
        return;

    let cursor = getCursor();
    if (cursor[0] === cursor[2] && cursor[1] === cursor[3])
        return;

    let line = findById(lines, id);
    if (line === null)
        return;

    let texts = splitTexts(line.texts, cursor);
    let start = textLength(texts[0]);
    let end = start + textLength(texts[1]);
    for (let i = 0; i < texts[1].length; i++) {
        if (!texts[1][i].style) {
            texts[1][i].style = name;
        } else if (texts[1][i].style.indexOf(name) > -1) {
            texts[1][i].style = texts[1][i].style.replaceAll(name, '');
        } else {
            texts[1][i].style += ' ' + name;
        }
        texts[0].push(texts[1][i]);
    }
    for (let text of texts[2])
        texts[0].push(text);
    mergeTexts(texts[0]);
    line.texts = texts[0];

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
    setCursor(id, cursor);
};

const textLength = (texts) => {
    let length = 0;
    for (let text of texts)
        length += text.text.length;

    return length;
};

export {
    bold,
    italic,
    underline,
    linethrough,
};