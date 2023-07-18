const messages = {
    en: {
        'placeholder': 'Please enter',
        'operate.add': 'Insert child',
        'operate.link': 'Link to',
        'operate.link.placeholder': 'eg: 1-2-3',
        'operate.link.ok': 'OK',
        'operate.unlink': 'Remove link',
        'operate.move.up': 'Move up',
        'operate.move.down': 'Move down',
        'operate.move.to': 'Move to',
        'operate.move.placeholder': 'eg: 1-2-3',
        'operate.move.ok': 'OK',
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