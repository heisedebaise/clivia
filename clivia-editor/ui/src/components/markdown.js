import { setCursor } from "./cursor";

const prefix = {
    '#': 'h1',
    '##': 'h2',
    '###': 'h3',
};

const markdown = (line) => {
    for (let key in prefix) {
        let text = line.texts[0].text;
        if (text.length > key.length && text.indexOf(key) === 0 && space(text.charCodeAt(key.length))) {
            line.texts[0].text = line.texts[0].text.substring(key.length + 1);
            line.tag = prefix[key];
            setCursor(line.id, [0, 0, 0, 0]);
        }
    }
};

const space = (code) => {
    return code === 32 || code === 160;
};

export {
    markdown
};