import { setCursor } from "./cursor";

const prefix = {
    '# ': 'h1',
    '## ': 'h2',
    '### ': 'h3',
};

const markdown = (line) => {
    for (let key in prefix) {
        if (line.texts[0].text.indexOf(key) === 0) {
            line.texts[0].text = line.texts[0].text.substring(key.length);
            line.tag = prefix[key];
            setCursor(line.id, [0, 0, 0, 0]);
        }
    }
};

export {
    markdown
};