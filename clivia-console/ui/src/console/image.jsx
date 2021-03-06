import React from 'react';
import { Upload, Modal } from 'antd';
import { PlusOutlined, LoadingOutlined } from '@ant-design/icons';
import { url, upload } from '../http';
import './image.css';

class Image extends React.Component {
    state = {
        uri: null,
        changed: false,
        loading: false,
        preview: null,
        remove: 0
    };

    upload = uploader => {
        this.setState({ loading: true });
        upload(this.props.upload, uploader.file).then(data => {
            if (data === null) {
                this.setState({ loading: false });

                return;
            }

            let uri = this.state.changed ? this.state.uri : this.props.value;
            let path = data.thumbnail || data.path;
            uri = uri ? (uri + ',' + path) : path;
            this.setState({
                uri: uri,
                changed: true,
                loading: false
            }, () => {
                this.props.form.value(this.props.name, this.state.uri)
            });
        });
    }

    preview = file => {
        this.setState({ preview: file.url });
    }

    cancel = () => {
        this.setState({ preview: null });
    }

    remove = file => {
        if (this.props.readonly) return;

        let uri = this.state.changed ? this.state.uri : this.props.value;
        if (!uri) return;

        let uris = uri.split(',');
        let u = '';
        for (let i in uris) {
            if (i === file.uid) continue;

            if (u.length > 0) u += ',';
            u += uris[i];
        }
        this.setState({
            uri: u,
            changed: true
        }, () => this.props.form.value(this.props.name, this.state.uri));
    }

    render = () => {
        let uri = this.state.changed ? this.state.uri : this.props.value;
        if (!this.state.changed && this.props.value) {
            this.props.form.value(this.props.name, this.props.value);
        }
        let list = [];
        if (uri) {
            let uris = uri.split(',');
            for (let i in uris) {
                list.push({
                    uid: '' + i,
                    name: uris[i],
                    url: url(uris[i]),
                    status: 'done'
                });
            }
        }

        let props = {
            listType: 'picture-card',
            fileList: list,
            className: 'image-uploader',
            customRequest: this.upload,
            onPreview: this.preview
        }
        if (!this.props.readonly)
            props.onRemove = this.remove;

        return (
            <div className="clearfix">
                <Upload {...props} >
                    {this.props.readonly || (this.props.size > 0 && list.length >= this.props.size) ? null : (this.state.loading ? <LoadingOutlined /> : <PlusOutlined />)}
                </Upload>
                <Modal visible={this.state.preview != null} footer={null} onCancel={this.cancel}>
                    <img alt="preview" style={{ width: '100%' }} src={this.state.preview} />
                </Modal>
            </div>
        );
    }
}

export default Image;