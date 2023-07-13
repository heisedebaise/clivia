const messages = {
    en: {
        'placeholder': 'Please enter',
        'operate.add': 'Insert child',
        'operate.link': 'Link to',
        'operate.link.placeholder': 'eg: 1-2-3',
        'operate.link.ok':'OK',
        'operate.unlink': 'Remove link',
        'operate.remove': 'Remove this'
    },
    zh: {},
    jp: {},
};

let language = 'en';

const message = (key) => {
    return messages[language][key] || '';
}

export {
    message
}