import { store } from '../store';
import { upload } from '../http';
import { now } from './time';
import { newId } from './generator';
import { message } from './locale';
import { findIndex } from './line';
import { findEventId } from './event';

const data = {
    id: null,
};

const selectImage = (uploader, e) => {
    data.id = e.target.parentNode.id;
    uploader.click();
};

const uploadImage = (e) => {
    let index = findIndex(data.id);
    for (let i = 0; i < e.target.files.length; i++) {
        store.lines.splice(index + i, i === 0 ? 1 : 0, {
            id: newId(),
            tag: 'image',
            uploading: message('image.uploading'),
            time: '',
        });
        upload('clivia.editor.image', e.target.files[i], data => {
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

const imageName = (e) => {
    let id = findEventId(e);
    if (id === null)
        return;

    let line = store.lines[findIndex(id)];
    line.name = e.target.innerText;
    line.time = now();
};

export {
    selectImage,
    uploadImage,
    imageName,
};