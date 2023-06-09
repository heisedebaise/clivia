import { store } from '../store';
import { now } from './time';
import { isComposition } from './composition';
import { annotation } from './annotation';
import { message } from './locale';
import { findById, isEmpty, mergeTexts } from './line';
import { setTag } from './keydown';
import { findEventId, findIdNode } from "./event";
import { focus, getCursorSync, setCursor } from './cursor';
import { markdown } from './markdown';

const keyup = (workspace, tag, e) => {
    if ((e.key === 'ArrowUp' || e.key === 'ArrowDown') && tag.arrow(e))
        return;

    let line = findById(findEventId(e));
    if (isComposition()) {
        if (window.safari && isEmpty(line.texts))
            line.texts[0].text = '.';
        if (e.code === 'Slash')
            showTag(workspace, tag, e);

        return;
    }

    let cursor = [];
    if (window.safari)
        cursor = getCursorSync();
    let indexes = [];
    for (let i = 0; i < e.target.children.length; i++) {
        let child = e.target.children[i];
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
        if (window.safari && index === 0 && text.indexOf('.') === 0)
            text = text.substring(1);
        if (line.texts[index].text === text)
            continue;

        line.texts[index].text = text;
    }
    if (e.key === 'Backspace' || e.key === 'Delete') {
        for (let i = line.texts.length - 1; i >= 0; i--)
            if (!indexes[i])
                line.texts.splice(i, 1);
        mergeTexts(line.texts);
        if (isEmpty(line.texts)) {
            store.placeholder = line.id;
            setCursor(line.id, [0, 0, 0, 0]);
        }
    }
    markdown(line);
    line.time = now();
    if (window.safari)
        setCursor(line.id, cursor);
    else
        focus();
    annotation();
    if (e.code === 'Slash')
        showTag(workspace, tag, e);
    else
        tag.hide();
};

const showTag = (workspace, tag, e) => {
    let node = findIdNode(e);
    if (node === null)
        return;

    tag.show(workspace, node);
    setTag(tag);
};

export {
    keyup,
};