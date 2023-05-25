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

const uploadImage = (lines, e) => {
    let index = findIndex(lines, data.id);
    for (let i = 0; i < e.target.files.length; i++) {
        lines.splice(index + i, i === 0 ? 1 : 0, {
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
            let line = lines[index + i];
            line.name = name;
            line.path = data.path;
            line.time = now();
            delete line.uploading;
        });
    }
};

const imageName = (lines, e) => {
    let id = findEventId(e);
    if (id === null)
        return;

    let line = lines[findIndex(lines, id)];
    line.name = e.target.innerText;
    line.time = now();
};

export {
    selectImage,
    uploadImage,
    imageName,
};