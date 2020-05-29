import React from 'react';
import { Upload, Button, message } from 'antd';
import { UploadOutlined, LoadingOutlined } from '@ant-design/icons';
import { url, service } from '../http';
import { toArray } from './json';

class File extends React.Component {
    state = {
        list: null,
        loading: false
    }

    upload = uploader => {
        this.setState({ loading: true });

        let reader = new FileReader();
        reader.onload = () => {
            if (!reader.result || typeof reader.result !== 'string') {
                return;
            }

            service('/photon/ctrl/upload', {
                name: this.props.upload,
                fileName: uploader.file.name,
                contentType: uploader.file.type,
                base64: reader.result.substring(reader.result.indexOf(',') + 1)
            }).then(data => {
                this.setState({ loading: false });
                if (data === null) return;

                if (!data.success) {
                    message.warn(data.message);

                    return
                }

                let list = this.list();
                list.push({
                    uid: '' + list.length,
                    name: data.fileName,
                    uri: data.path,
                    url: url(data.path),
                    status: 'done'
                });
                this.setState({ list: list }, this.value);
            });
        };
        reader.readAsDataURL(uploader.file);
    }

    remove = file => {
        let list = [];
        for (let f of this.list())
            if (f.uid !== file.uid)
                list.push(f);
        this.setState({ list: list }, this.value);
    }

    value = () => {
        let list = [];
        for (let file of this.state.list) {
            list.push({
                name: file.name,
                uri: file.uri
            });
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

    render = () => {
        return (
            <Upload fileList={this.list()} customRequest={this.upload} onRemove={this.remove}>
                {this.state.loading ? <Button disabled={true}><LoadingOutlined /> 上传中</Button> : <Button><UploadOutlined /> 上传</Button>}
            </Upload>
        );
    }
}

export default File;