const messages = {
    en: {
        'placeholder': 'Please enter',
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