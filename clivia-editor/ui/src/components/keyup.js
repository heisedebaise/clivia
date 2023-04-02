import { findById } from './line';
import { findEventId } from "./event";
import { composition } from './composition';
import { setCursor } from './cursor';
import { clearBr } from './html';

const keyup = (lines, e) => {
    if (composition())
        return;

    let selection = getSelection();
    if (selection && selection.rangeCount > 0 && selection.focusNode.nodeName === '#text' && selection.focusNode.data != '' && selection.focusNode.parentElement.id) {
        let line = findById(lines, selection.focusNode.parentElement.id);
        let data = selection.focusNode.data;
        line.texts[0].text = data;
        selection.focusNode.parentElement.removeChild(selection.focusNode);
        setCursor(line.id, 0, data.length, 0, data.length);
    } else {
        let line = findById(lines, findEventId(e));
        let indexes = [];
        for (let i = 0; i < e.target.children.length; i++) {
            let index = parseInt(e.target.children[i].dataset.index);
            indexes[index] = 1;
            line.texts[index].text = e.target.children[i].innerText;
        }
        setTimeout(() => {
            for (let i = line.texts.length - 1; i >= 0; i--) {
                if (!indexes[i]) {
                    console.log(i);
                    line.texts.splice(i, 1);
                }
            }
        }, 1000);
    }
};

export {
    keyup
};