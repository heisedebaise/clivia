import { findId } from "./event";
import { composition } from './composition';

const keyup = (lines, e) => {
    if (composition())
        return;

    let index = parseInt(e.target.dataset.index);
    let id = findId(e);
    for (let line of lines) {
        if (line.id === id) {
            line.texts[index].text = e.target.innerText;

            break;
        }
    }
};

export {
    keyup
};