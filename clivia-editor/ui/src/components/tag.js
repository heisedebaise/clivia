import { nextTick } from "vue";
import { store } from "../store";
import { now } from "./time";
import { message } from './locale';
import { trigger } from "./event";
import { findLine, findIndex, isEmpty } from "./line";
import { setCursor, getSelect } from "./cursor";

const newText = (tag, text) => {
    return {
        id: newId(),
        tag: tag || 'p',
        texts: [{
            text: text || '',
        }],
        time: now(),
    };
};

const newImage = (path, name) => {
    let index = findIndex(store.focus);
    let image = {
        id: newId(),
        tag: 'image',
        time: now(),
    };
    if (path)
        image.path = path;
    else
        image.upload = message('image.upload');
    if (name)
        image.name = name;
    if (isEmpty(store.lines[index].texts))
        store.lines.splice(index, 1, image);
    else
        store.lines.splice(index + 1, 0, image);
    last();
    trigger('annotation');

    return image;
};

const newDivider = () => {
    let id = newId();
    store.lines.splice(findIndex(store.focus) + 1, 0, {
        id: id,
        tag: 'divider',
        time: now(),
    });
    last();
    store.focus = id;
    nextTick(() => trigger('focus'));
    trigger('annotation');
};

const changeTag = (tag, depth) => {
    let lines = getSelect();
    if (lines.length > 0) {
        for (let line of lines)
            changeLineTag(line, tag, depth);

        return true;
    }

    if (!changeLineTag(findLine(store.focus), tag, depth))
        return false;

    setCursor();

    return true;
};

const changeLineTag = (line, tag, depth) => {
    if (!line)
        return false;

    let index = findIndex(line.id);
    if (tag === 'ol-ul') {
        if (store.lines.length > 1)
            tag = store.lines[index === 0 ? 1 : (index - 1)].tag;
        if (tag != 'ol' && tag != 'ul')
            tag = 'ol';
    }

    line.tag = tag;
    if ((tag === 'h1' || tag === 'h2' || tag === 'h3' || tag === 'p' || tag === 'ul' || tag === 'ol') && !line.texts)
        line.texts = [{ text: '' }];
    if (tag === 'ul' || tag === 'ol') {
        line.depth = (line.depth || 0) + depth;
        if (line.depth === 0)
            line.depth = 1;
    }
    line.time = now();

    return true;
};

const olTypes = [
    ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'],
    ['i', 'ii', 'iii', 'iv', 'v', 'vi', 'vii', 'viii', 'ix', 'x', 'xi', 'xii', 'xiii', 'xiv', 'xv', 'xvi', 'xvii', 'xvii', 'xix', 'xx']
];
const ulTypes = ['disc', 'circle', 'square'];

const resetList = () => {
    let decimal = [];
    for (let line of store.lines) {
        if (line.tag === 'ol' || line.tag === 'ul') {
            let style = 'margin-left:' + line.depth + 'rem;';
            let depth = line.depth - 1;
            let d = depth % 3;
            if (line.tag === 'ul')
                style += 'list-style-type:' + ulTypes[d];
            else {
                decimal[depth] = (decimal[depth] || 0) + 1;
                let type = decimal[depth];
                if (d > 0) {
                    let types = olTypes[d - 1];
                    type = types[(decimal[depth] - 1) % types.length];
                }
                style += 'list-style-type:\'' + type + '. \'';
            }
            line.listStyle = style;
        } else {
            decimal = [];
            delete line.depth;
            delete line.listStyle;
        }
    }
};

const newId = () => {
    for (let i = 0; i < 1024; i++) {
        let id = random('id', 16);
        if (!document.querySelector('#' + id))
            return id;
    }

    return random(16);
};

const random = (prefix, length) => {
    let string = prefix;
    while (string.length < length)
        string += Math.random().toString(36).substring(2);

    return string.substring(0, length);
};

const last = () => {
    let line = store.lines[store.lines.length - 1];
    if (!line.texts)
        store.lines.push(newText());
};

export {
    newText,
    newImage,
    newDivider,
    changeTag,
    resetList,
    newId,
    last,
}