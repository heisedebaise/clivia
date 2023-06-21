import { now } from "./time";

const newText = (tag, text) => {
    return {
        id: newId(),
        tag: tag,
        texts: [{
            text: text || '',
        }],
        time: now(),
    };
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

export {
    newText,
    newId,
}