const messages = {
    en: {
        'placeholder.text': "Type '/' for commands",
        'placeholder.h1': 'Heading 1',
        'placeholder.h2': 'Heading 2',
        'placeholder.h3': 'Heading 3',
        'placeholder.img': 'Add images',
        'placeholder.annotation': 'Please enter a label',
        'tag.text': 'Text',
        'tag.text.sub': 'Just start writing with plain text.',
        'tag.h1': 'Heading 1',
        'tag.h1.sub': 'Big section heading.',
        'tag.h2': 'Heading 2',
        'tag.h2.sub': 'Medium section heading.',
        'tag.h3': 'Heading 3',
        'tag.h3.sub': 'Small section heading.',
        'tag.img': 'Image',
        'tag.img.sub': 'Upload images.',
        'tag.ai-text': 'AI Text',
        'tag.ai-text.sub': 'ai text',
        'tag.ai-image': 'AI Image',
        'tag.ai-image.sub': 'ai image',
        'tag.divider': 'Divider',
        'tag.divider.sub': 'Visually divide blocks.',
        'image.upload': 'Click to upload picture',
        'image.uploading': 'uploading',
        'operate.move.up': 'Move up one line',
        'operate.move.down': 'Move one line down',
        'operate.delete': 'Delete this line',
        'ai.go': 'GO',
        'ai.go.1': 'One',
        'ai.go.2': 'Tow',
        'ai.go.4': 'Four',
        'ai.wait': 'AI robot is thinking',
        'ai.empty': 'AI bot didn\'t reply',
        'ai.insert': 'Insert',
        'ai.generate': 'AI generate',
        'error.empty': 'data error',
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