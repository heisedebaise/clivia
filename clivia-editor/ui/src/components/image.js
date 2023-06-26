import { store } from '../store';
import { upload } from '../http';
import { now } from './time';
import { message } from './locale';
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
        upload('clivia.editor.image', event.target.files[i], data => {
            let name = data.fileName;
            let indexOf = name.lastIndexOf('.');
            if (indexOf > -1)
                name = name.substring(0, indexOf);
            let line = store.lines[index + i];
            line.name = name;
            line.path = data.path;
            line.time = now();
            delete line.uploading;
        }, progress => {
            store.lines[index + i].uploading = message('image.uploading') + ' ' + progress + '%';
        });
    }
};

const imageName = (event) => {
    let line = store.lines[findIndex(store.focus)];
    line.name = event.target.innerText;
    line.time = now();
};

export {
    selectImage,
    uploadImage,
    imageName,
};