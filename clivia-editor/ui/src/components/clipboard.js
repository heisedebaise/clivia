import { store } from '../store';
import { now } from './time';
import { getCursor, getSelect, setCursor } from './cursor';
import { findIndex, findLine, splitTexts, mergeTexts } from './line';
import { newText } from './tag';
import { markdown } from './markdown';
import { uploadImageBlob } from './image';

const copy = (event) => {
    if (event)
        event.preventDefault();

    let lines = getSelect();
    if (lines.length > 0) {
        let texts = '';
        for (let line of lines) {
            if (!line.texts)
                continue;

            for (let text of line.texts)
                texts += text.text;
            texts += '\n';
        }
        navigator.clipboard.writeText(texts.trim());

        return;
    }

    let line = findLine(store.focus);
    if (line.tag === 'image') {
        let image = document.querySelector('#' + line.id + ' img');
        if (!image)
            return;

        let canvas = document.createElement('canvas');
        canvas.width = image.width;
        canvas.height = image.height;
        canvas.getContext('2d').drawImage(image, 0, 0);
        canvas.toBlob(blob => navigator.clipboard.write([new ClipboardItem({ 'image/png': blob })]), 'image/png');

        return;
    }

    let cursor = getCursor();
    if (cursor[0] === cursor[2] && cursor[1] === cursor[3])
        return;

    let text = '';
    if (cursor[0] === cursor[2])
        text = line.texts[cursor[0]].text.substring(cursor[1], cursor[3]);
    else {
        text = line.texts[cursor[0]].text.substring(cursor[1]);
        for (let i = cursor[0] + 1; i < cursor[2]; i++)
            text += line.texts[i].text;
        text += line.texts[cursor[2]].text.substring(0, cursor[3]);
    }
    navigator.clipboard.writeText(text);
};

const paste = (event) => {
    if (event)
        event.preventDefault();

    navigator.clipboard.read().then(items => {
        for (let item of items) {
            for (let type of item.types) {
                item.getType(type).then(blob => {
                    if (type === 'text/plain') {
                        blob.text().then(pasteText);
                    } else if (type.indexOf('image/') === 0) {
                        uploadImageBlob(blob, type.replace(/\//g, '.'));
                    }
                });
            }
        }
    });
};

const pasteText = (text) => {
    let index = findIndex(store.focus);
    if (text.indexOf('\n') === -1) {
        text = text.trim();
        let line = store.lines[index];
        let cursor = getCursor();
        let texts = splitTexts(line.texts, cursor);
        if (texts[1].length === 0) {
            texts[0][texts[0].length - 1].text += text;
            cursor[3] += text.length;
        } else {
            texts[1][0].text = text;
            texts[0].push(texts[1][0]);
            for (let t of texts[2])
                texts[0].push(t);
            mergeTexts(texts[0]);
            cursor[3] = cursor[1] + text.length;
        }
        line.texts = texts[0];
        line.time = now();
        setCursor(store.focus, cursor);
    } else {
        for (let t of text.split('\n')) {
            let line = newText('p', t.trim());
            markdown(line);
            store.lines.splice(++index, 0, line);
        }
    }
};

export {
    copy,
    paste,
}