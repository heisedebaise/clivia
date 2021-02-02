import React from 'react';
import { message } from 'antd';
import { url } from '../http';
import { toArray } from '../json';

class UploadSupport extends React.Component {
    action = url('/photon/ctrl-http/upload');

    change = ({ file }) => {
        if (file.status === 'uploading') {
            let list = this.list();
            for (let f of list)
                if (f.uid === file.uid)
                    return;

            list.push(file);
            this.setState({ list });

            return;
        }

        if (file.status === 'done') {
            if (file.response.success) {
                this.value();

                return;
            }

            let list = [];
            for (let f of this.list())
                if (f.uid !== file.uid)
                    list.push(f);
            this.setState({ list });
            message.warn(file.response.message);

            return;
        }

        if (file.status === 'removed') {
            let list = [];
            for (let f of this.list()) {
                if (f.uid === file.uid)
                    continue;

                list.push(f);
            }
            this.setState({ list: list });
            this.value();

            return;
        }
    }

    value = () => {
        let list = [];
        for (let file of this.state.list) {
            if (!file.uri) {
                file.uri = file.response.path;
                if (file.response.thumbnail)
                    file.thumbnail = file.response.thumbnail;
                file.url = url(file.uri);
            }
            let f = {
                name: file.name,
                uri: file.uri
            };
            if (file.thumbnail)
                f.thumbnail = file.thumbnail;
            list.push(f);
        }
        this.props.form.value(this.props.name, JSON.stringify(list));
    }

    list = () => {
        if (this.state.list !== null)
            return this.state.list;

        let list = this.props.value ? toArray(this.props.value) : [];
        for (let i = 0; i < list.length; i++) {
            list[i].uid = '' + i;
            list[i].url = url(list[i].uri);
            list[i].status = 'done';
        }

        return list;
    }
}

export default UploadSupport;