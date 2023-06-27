import { store } from "../store";

const findLine = (id) => {
    for (let line of store.lines)
        if (line.id === id)
            return line;

    return null;
};

const findIndex = (id) => {
    for (let i = 0; i < store.lines.length; i++)
        if (store.lines[i].id === id)
            return i;

    return 0;
};

const findByXy = (workspace, x, y) => {
    if (store.vertical) {
        for (let line of store.lines) {
            let node = document.querySelector('#' + line.id).parentElement;
            let left = node.offsetLeft - workspace.scrollLeft;
            if (x >= left && x < left + node.offsetWidth)
                return line.id;
        }
    } else {
        for (let line of store.lines) {
            let node = document.querySelector('#' + line.id).parentElement;
            let top = node.offsetTop - workspace.scrollTop;
            if (y >= top && y < top + node.offsetHeight)
                return line.id;
        }
    }

    return null;
};

const splitTexts = (texts, cursor) => {
    if (isEmpty(texts))
        return [[{ text: '' }], [{ text: '' }], [{ text: '' }]];

    if (cursor[0] >= texts.length)
        cursor[0] = texts.length - 1;
    if (cursor[2] >= texts.length)
        cursor[2] = texts.length - 1;
    if (cursor[1] > texts[cursor[0]].text.length)
        cursor[1] = texts[cursor[0]].text.length;
    if (cursor[3] > texts[cursor[2]].text.length)
        cursor[3] = texts[cursor[2]].text.length;

    let array = [[], [], []];
    for (let i = 0; i < texts.length; i++) {
        let text = texts[i];
        if (i < cursor[0]) {
            array[0].push(text);

            continue;
        }

        if (i > cursor[0] && i < cursor[2]) {
            array[1].push(text);

            continue;
        }

        if (i > cursor[2]) {
            array[2].push(text);

            continue;
        }

        if (i === cursor[0]) {
            if (cursor[1] > 0) {
                let txt = { ...text };
                txt.text = txt.text.substring(0, cursor[1]);
                array[0].push(txt);
            }
            if (cursor[1] < text.text.length) {
                let txt = { ...text };
                txt.text = txt.text.substring(cursor[1], cursor[0] === cursor[2] ? cursor[3] : txt.text.length);
                array[1].push(txt);
            }
        }
        if (i === cursor[2]) {
            if (cursor[0] < cursor[2] && cursor[3] > 0) {
                let txt = { ...text };
                txt.text = txt.text.substring(0, cursor[3]);
                array[1].push(txt);
            }
            if (cursor[3] < text.text.length) {
                let txt = { ...text };
                txt.text = txt.text.substring(cursor[3]);
                array[2].push(txt);
            }
        }
    }

    for (let i = 0; i < array.length; i++)
        mergeTexts(array[i]);

    return array;
};

const mergeTexts = (texts) => {
    for (let i = texts.length - 1; i >= 0; i--) {
        if (i > 0 && texts[i].text.length === 0) {
            texts.splice(i, 1);
        } else if (texts[i].style && texts[i].style.indexOf(' ') > -1) {
            texts[i].style = texts[i].style.trim().replace(/ +/g, ' ').split(' ').sort().join(' ');
        }
    }
    for (let i = texts.length - 1; i > 0; i--) {
        if (texts[i].style === texts[i - 1].style && texts[i].annotation === texts[i - 1].annotation) {
            texts[i - 1].text += texts[i].text;
            texts.splice(i, 1);
        }
    }
};

const isEmpty = (texts) => {
    return texts && (texts.length === 0 || (texts.length === 1 && texts[0].text.length === 0));
};

export {
    findLine,
    findIndex,
    findByXy,
    splitTexts,
    mergeTexts,
    isEmpty,
};