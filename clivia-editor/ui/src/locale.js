const messages = {
    en: {
        'placeholder': "Type '/' for commands",
        'placeholder.h1': 'Heading 1',
        'placeholder.h2': 'Heading 2',
        'placeholder.h3': 'Heading 3',
        'tag.text': 'Text',
        'tag.text.sub': 'Just start writing with plain text.',
        'tag.h1': 'Heading 1',
        'tag.h1.sub': 'Big section heading.',
        'tag.h2': 'Heading 2',
        'tag.h2.sub': 'Medium section heading.',
        'tag.h3': 'Heading 3',
        'tag.h3.sub': 'Small section heading.',
        'tag.img': 'Image',
        'tag.img.sub': 'Upload picture.',
        'tag.divider': 'Divider',
        'tag.divider.sub': 'Visually divide blocks.',
    },
    zh: {}
};

let language = 'en';

const message = (key, dv) => {
    return messages[language][key] || messages[language][dv] || '';
}

export {
    message
}