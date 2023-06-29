import { store } from '../store';
import { upload } from '../http';
import { now } from './time';
import { message } from './locale';
import { trigger } from './event';
import { findIndex } from './line';
import { findEventId } from './event';
import { newId } from './tag';

const selectImage = (uploader, event) => {
    store.focus = findEventId(event);
    uploader.click();
};

const uploadImage = (event) => {
    let index = findIndex(store.focus);
    for (let i = 0; i < event.target.files.length; i++) {
        store.lines.splice(index + i, i === 0 ? 1 : 0, {
            id: newId(),
            tag: 'image',
            uploading: message('image.uploading'),
            time: '',
        });
        upload('clivia.editor.image', event.target.files[i], null, data => {
            let name = data.fileName;
            let indexOf = name.lastIndexOf('.');
            if (indexOf > -1)
                name = name.substring(0, indexOf);
            let line = store.lines[index + i];
            line.name = name;
            line.path = data.path;
            line.time = now();
            delete line.uploading;
            trigger('annotation');
        }, progress => {
            store.lines[index + i].uploading = message('image.uploading') + ' ' + progress + '%';
        });
    }
};

const uploadImageBlob = (blob, name) => {
    let index = findIndex(store.focus) + 1;
    store.lines.splice(index, 0, {
        id: newId(),
        tag: 'image',
        uploading: message('image.uploading'),
        time: '',
    });
    upload('clivia.editor.image', blob, name, data => {
        let name = data.fileName;
        let indexOf = name.lastIndexOf('.');
        if (indexOf > -1)
            name = name.substring(0, indexOf);
        let line = store.lines[index];
        line.name = name;
        line.path = data.path;
        line.time = now();
        delete line.uploading;
        trigger('annotation');
    }, progress => {
        store.lines[index].uploading = message('image.uploading') + ' ' + progress + '%';
    });
};

const imageName = (event) => {
    let line = store.lines[findIndex(store.focus)];
    line.name = event.target.innerText;
    line.time = now();
};

export {
    selectImage,
    uploadImage,
    uploadImageBlob,
    imageName,
};