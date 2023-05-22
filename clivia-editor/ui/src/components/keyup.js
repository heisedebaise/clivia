import { now } from './time';
import { annotation } from './handler';
import { message } from './locale';
import { findById, isEmpty, mergeTexts } from './line';
import { setTag } from './keydown';
import { findEventId, findIdNode } from "./event";
import { focus, setCursor } from './cursor';
import { markdown } from './markdown';

const data = {
    composition: false,
};

const compositionstart = (e) => {
    data.composition = true;
};

const compositionend = (lines, workspace, tag, placeholder, e) => {
    data.composition = false;
    keyup(lines, workspace, tag, placeholder, e);
};

const keyup = (lines, workspace, tag, placeholder, e) => {
    if ((e.key === 'ArrowUp' || e.key === 'ArrowDown') && tag.arrow(e))
        return;

    if (data.composition) {
        if (e.code === 'Slash')
            showTag(workspace, tag, e);

        return;
    }

    let selection = getSelection();
    if (selection && selection.rangeCount > 0 && selection.focusNode.nodeName === '#text' && selection.focusNode.data != '' && selection.focusNode.parentElement.id) {
        let line = findById(lines, selection.focusNode.parentElement.id);
        let data = selection.focusNode.data;
        line.texts[0].text = data;
        line.time = now();
        selection.focusNode.parentElement.removeChild(selection.focusNode);
        setCursor(line.id, [0, data.length, 0, data.length]);
    } else {
        let line = findById(lines, findEventId(e));
        let indexes = [];
        for (let i = 0; i < e.target.children.length; i++) {
            let child = e.target.children[i];
            if (child.className === 'placeholder') {
                if (child.innerText.length > placeholder.text.length) {
                    line.texts[line.texts.length - 1].text = child.innerText.substring(0, child.innerText.length - placeholder.text.length)
                    placeholder.id = '';
                }

                continue;
            }

            let index = parseInt(child.dataset.index);
            indexes[index] = 1;
            line.texts[index].text = child.innerText;
        }
        if (e.key === 'Backspace' || e.key === 'Delete') {
            for (let i = line.texts.length - 1; i >= 0; i--)
                if (!indexes[i])
                    line.texts.splice(i, 1);
            mergeTexts(line.texts);
            if (isEmpty(line.texts)) {
                placeholder.id = line.id;
                placeholder.text = message('placeholder.' + line.tag);
                setCursor(line.id, [0, 0, 0, 0]);
            }
        }
        markdown(line);
        line.time = now();
    }
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
    compositionstart,
    compositionend,
    keyup,
};