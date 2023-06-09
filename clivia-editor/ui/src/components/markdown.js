import { annotation } from "./annotation";
import { setCursor } from "./cursor";
import { style } from "./style";

const prefix = {
    '#': 'h1',
    '##': 'h2',
    '###': 'h3',
};
const pair = {
    ' * ': 'italic',
    ' _ ': 'italic',
    ' ** ': 'bold',
    ' __ ': 'bold',
    ' *** ': 'underline',
    ' ___ ': 'underline'
};
const divider = {
    '---': true,
    '***': true,
    '___': true,
};

const markdown = (line) => {
    for (let key in prefix) {
        let text = line.texts[0].text;
        if (text.length > key.length && text.indexOf(key) === 0 && space(text.charCodeAt(key.length))) {
            line.texts[0].text = line.texts[0].text.substring(key.length + 1);
            line.tag = prefix[key];
            annotation();
            setCursor(line.id, [0, 0, 0, 0]);

            return;
        }
    }

    for (let key in pair) {
        let length = key.length;
        let range = [];
        for (let i = 0; i < line.texts.length; i++) {
            let text = line.texts[i].text;
            let indexOf = text.indexOf(key);
            if (indexOf === -1)
                continue;

            range.push(i);
            range.push(indexOf);
            if (range.length === 4)
                break;

            indexOf = text.indexOf(key, indexOf + length + 1);
            if (indexOf === -1)
                continue;

            range.push(i);
            range.push(indexOf);
            break;
        }
        if (range.length === 4) {
            if (range[0] === range[2]) {
                let text = line.texts[range[0]].text;
                line.texts[range[0]].text = text.substring(0, range[1]) + text.substring(range[1] + length, range[3]) + text.substring(range[3] + length);
                range[3] -= length;
            } else {
                let text = line.texts[range[0]].text;
                line.texts[range[0]].text = text.substring(0, range[1]) + text.substring(range[1] + length);
                text = line.texts[range[2]].text;
                line.texts[range[2]].text = text.substring(0, range[3]) + text.substring(range[3] + length);
            }
            style(line, range, pair[key]);

            return;
        }
    }

    if (line.texts.length === 1 && divider[line.texts[0].text]) {
        line.tag = 'divider';
        annotation();
        delete line.texts;
    }
};

const space = (code) => {
    return code === 32 || code === 160;
};

export {
    markdown
};