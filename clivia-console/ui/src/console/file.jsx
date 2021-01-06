import React from 'react';
import { Upload, Button, message } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import { url } from '../http';
import { toArray } from '../json';

class File extends React.Component {
    state = {
        list: null
    }

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
                file.url = url(file.uri);
            }
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
        let props = {
            action: url('/photon/ctrl-http/upload'),
            name: this.props.upload,
            multiple: true,
            progress: {
                strokeColor: {
                    '0%': '#108ee9',
                    '100%': '#87d068',
                },
                strokeWidth: 3,
                format: percent => `${parseFloat(percent.toFixed(2))}%`,
            },
            fileList: this.list(),
            onChange: this.change
        };

        return (
            <Upload {...props}>
                <Button><UploadOutlined /> 上传</Button>
            </Upload>
        );
    }
}

export default File;