const messages = {
    en: {
        'placeholder.h1': 'Heading 1',
        'placeholder.h2': 'Heading 2',
        'placeholder.h3': 'Heading 3',
        'placeholder.p': "Type '/' for commands",
        'placeholder.ol': "Type '/' for commands",
        'placeholder.ul': "Type '/' for commands",
        'placeholder.img': 'Add images',
        'placeholder.annotation': 'Please enter a label',
        'tag.h1': 'Heading 1',
        'tag.h1.sub': 'Big section heading.',
        'tag.h2': 'Heading 2',
        'tag.h2.sub': 'Medium section heading.',
        'tag.h3': 'Heading 3',
        'tag.h3.sub': 'Small section heading.',
        'tag.p': 'Text',
        'tag.p.sub': 'Just start writing with plain text.',
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
        'operate.forground': 'Forground color',
        'operate.background': 'Background color',
        'operate.jump-to': 'Jump to',
        'operate.move.up': 'Move up one line',
        'operate.move.down': 'Move one line down',
        'operate.remove': 'Delete this line',
        'operate.ai': 'AI-assisted',
        'operate.ai.text': 'Text',
        'operate.ai.image': 'Image',
        'annotation.placeholder': 'Please enter a label',
        'annotation.ok': 'OK',
        'annotation.remove': 'Remove',
        'ai.go': 'GO',
        'ai.go.1': 'One',
        'ai.go.2': 'Tow',
        'ai.go.4': 'Four',
        'ai.wait': 'AI robot is thinking',
        'ai.empty': 'AI bot didn\'t reply',
        'ai.insert': 'Insert',
        'ai.generate': 'AI generate',
        'search.find': 'Find',
        'search.replace': 'Replace',
        'search.empty': 'No results',
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