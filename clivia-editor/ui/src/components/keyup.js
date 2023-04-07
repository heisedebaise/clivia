import { now } from './time';
import { findById, mergeTexts } from './line';
import { findEventId } from "./event";
import { composition } from './composition';
import { focus, setCursor } from './cursor';
import { markdown } from './markdown';

const keyup = (lines, e) => {
    if (composition())
        return;

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
            let index = parseInt(e.target.children[i].dataset.index);
            indexes[index] = 1;
            line.texts[index].text = e.target.children[i].innerText;
        }
        if (e.key === 'Backspace' || e.key === 'Delete') {
            for (let i = line.texts.length - 1; i >= 0; i--)
                if (!indexes[i])
                    line.texts.splice(i, 1);
            mergeTexts(line.texts);
        }
        markdown(line);
        line.time = now();
    }
    focus();
};

export {
    keyup
};