import { store } from '../store';
import { message } from './locale';
import { now } from './time';
import { findEventId, trigger } from './event';
import { findLine, isEmpty, mergeTexts } from './line';
import { getCursorSync, setCursor, selectAll } from './cursor';
import { isComposition } from './composition';
import { markdown } from './markdown';

const keyup = (event) => {
    if (event.key === 'ArrowUp' || event.key === 'ArrowDown' || event.key === 'Control')
        return;

    if (event.ctrlKey && event.key === 'a') {
        selectAll();

        return;
    }

    let line = findLine(findEventId(event));
    if (isComposition()) {
        if (event.code === 'Slash')
            trigger('slash');

        return;
    }

    let cursor = getCursorSync();
    let indexes = [];
    for (let i = 0; i < event.target.children.length; i++) {
        let child = event.target.children[i];
        let text = child.innerText;
        if (child.className === 'placeholder') {
            let placeholder = message('placeholder.' + line.tag);
            if (text.length > placeholder.length)
                line.texts[0].text = text.substring(0, text.length - placeholder.length);
            if (!isEmpty(line.texts))
                store.placeholder = '';

            continue;
        }

        let index = parseInt(child.dataset.index);
        indexes[index] = 1;
        if (line.texts[index].text === text)
            continue;

        line.texts[index].text = text;
    }
    if (event.key === 'Backspace' || event.key === 'Delete') {
        for (let i = line.texts.length - 1; i >= 0; i--)
            if (!indexes[i])
                line.texts.splice(i, 1);
        if (isEmpty(line.texts)) {
            line.texts = [{ text: '' }];
            setCursor(line.id, [0, 0, 0, 0]);
        } else
            mergeTexts(line.texts);
    }
    markdown(line);
    line.time = now();
    setCursor(line.id, cursor);
    trigger('annotation');
    if (event.code === 'Slash')
        trigger('slash');
};

export {
    keyup,
};