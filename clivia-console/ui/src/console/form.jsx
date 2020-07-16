import React from 'react';
import { Form, Radio, Select, DatePicker, Switch, Input, Button, message } from 'antd';
import { PaperClipOutlined } from '@ant-design/icons';
import moment from 'moment';
import { service, url } from '../http';
import meta from './meta';
import { toMoney, fromMoney, toPercent, fromPercent } from './numeric';
import { toArray } from '../json';
import Image from './image';
import File from './file';
import DSelect from './dselect';
import Editor from './editor';
import Category from './category';
import User from './user';
import './form.css';

const layout = {
    labelCol: {
        xs: {
            span: 24
        },
        sm: {
            span: 6
        }
    },
    wrapperCol: {
        xs: {
            span: 24
        },
        sm: {
            span: 12
        }
    }
};

class Base extends React.Component {
    constructor(props) {
        super(props);

        this.form = React.createRef();
        this.state = props.data || {};
        this.format(this.state);
        this.values = {};
    }

    componentDidMount = () => {
        if (this.props.uri && !this.props.data) {
            this.load().then(data => {
                if (data === null) return;

                this.data(data);
            });
        }

        for (let prop of meta.props(this.props.props, this.props.meta.props)) {
            if (prop.type === 'agreement') {
                service('/keyvalue/object', { key: prop.agreement }).then(data => {
                    if (data === null) return;

                    let array = toArray(data[prop.agreement]);
                    if (array.length === 0) return;

                    data[prop.agreement] = array[0];
                    this.setState(data);
                });
            }
        }
    }

    data = data => {
        let values = this.form.current.getFieldsValue();
        for (let key in values) {
            values[key] = data[key];
        }
        this.format(values);
        this.form.current.setFieldsValue(values);
        this.setState(data);
    }

    format = (values) => {
        for (let prop of meta.props(this.props.props, this.props.meta.props)) {
            let value = values[prop.name];
            if (prop.labels)
                values[prop.name] = '' + value;
            else if (prop.type === 'money')
                values[prop.name] = toMoney(value, prop.empty);
            else if (prop.type === 'percent')
                values[prop.name] = toPercent(value);
            else if (prop.type === 'switch')
                values[prop.name] = value === 1;
            else if (value) {
                if (prop.type === 'date')
                    values[prop.name] = moment(value, 'YYYY-MM-DD');
                else if (prop.type === 'datetime')
                    values[prop.name] = moment(value, 'YYYY-MM-DD HH:mm:ss');
            }
        }
    }

    value = (name, value) => this.values[name] = value;

    button = mt => {
        let values = this.form.current.getFieldsValue();
        for (let prop of meta.props(this.props.props, this.props.meta.props)) {
            let value = values[prop.name];
            if (prop.type === 'switch') {
                values[prop.name] = value ? 1 : 0;

                continue;
            }

            if (!value) {
                delete values[prop.name];

                continue;
            };

            if (prop.type === 'date')
                values[prop.name] = value.format("YYYY-MM-DD");
            else if (prop.type === 'datetime')
                values[prop.name] = value.format("YYYY-MM-DD HH:mm:ss");
            else if (prop.type === 'money')
                values[prop.name] = fromMoney(value);
            else if (prop.type === 'percent')
                values[prop.name] = fromPercent(value);
        }
        if (this.props.data)
            for (let key in this.props.data)
                if (!(key in values))
                    values[key] = this.props.data[key];
        this.submit(mt, { ...values, ...this.values }).then(data => {
            if (data === null) return;

            if (mt.reload)
                this.props.body.load(this.props.uri, this.props.parameter, this.props.data);
            else if (mt.success)
                this.props.body.load(this.props.body.uri(this.props.uri, mt.success), this.props.parameter, this.props.data);
            else if (mt.data)
                this.data(data);
            else
                message.success('操作已完成！');
        });
    }

    cancel = mt => {
        this.props.body.load(this.props.body.uri(this.props.uri, mt.success), this.props.parameter);
    }

    render = () => {
        let items = [];
        if (this.props.meta.info)
            items.push(<div key={'info:' + this.props.meta.info} className="console-info" dangerouslySetInnerHTML={{ __html: this.state[this.props.meta.info] }} />);
        for (let prop of meta.props(this.props.props, this.props.meta.props)) {
            let item = {
                key: prop.name,
                className: 'console-form-item console-form-item-' + (items.length % 2 === 0 ? 'even' : 'odd'),
                label: prop.label
            };
            if (prop.type && prop.type.startsWith('read-only')) {
                items.push(<Form.Item {...item}>{this.readonly(prop)}</Form.Item>);
            } else if (prop.type === 'image') {
                items.push(<Form.Item {...item}><Image name={prop.name} upload={prop.upload} size={prop.size || 1} value={this.state[prop.name] || ''} form={this} /></Form.Item>);
            } else if (prop.type === 'file') {
                items.push(<Form.Item {...item}><File name={prop.name} upload={prop.upload} size={prop.size || 1} value={this.state[prop.name] || ''} form={this} /></Form.Item>);
            } else if (prop.type === 'dselect') {
                items.push(<Form.Item {...item}><DSelect list={prop.list} parameter={prop.parameter} name={prop.name} value={this.state[prop.name]} vname={prop.vname} lname={prop.lname} form={this} /></Form.Item>);
            } else if (prop.type === 'editor') {
                items.push(<Form.Item {...item}><Editor name={prop.name} value={this.state[prop.name] || ''} form={this} /></Form.Item>);
            } else if (prop.type === 'html') {
                items.push(<Form.Item {...item}><div dangerouslySetInnerHTML={{ __html: this.state[prop.name] || '' }} /></Form.Item>);
            } else if (prop.type === 'agreement') {
                let value = this.state[prop.agreement] || { uri: '', name: '' };
                if (value) {
                    let label = value.name;
                    let index = label.lastIndexOf('.');
                    if (index > -1) label = label.substring(0, index);
                    item.className += ' console-form-agreement';
                    item.label = 'agreement';
                    items.push(<Form.Item {...item}><a href={value.uri + '?filename=' + value.name} target="_blank" rel="noopener noreferrer">{label}</a></Form.Item>);
                }
            } else if (prop.type === 'category') {
                let list = prop.category;
                if (!list && this.props.parameter && this.props.parameter.key)
                    list = this.props.parameter.key;
                items.push(<Form.Item {...item}><Category list={list} pointTo={prop.pointTo} name={prop.name} value={this.state[prop.name]} form={this} /></Form.Item>);
            } else if (prop.type === 'user') {
                items.push(<Form.Item {...item}><User data={this.state[prop.name]} /></Form.Item>);
            } else {
                if (prop.type === 'switch')
                    item.valuePropName = 'checked';
                items.push(<Form.Item {...item} name={prop.name}>{this.input(prop)}</Form.Item>);
            }
        }

        return (
            <Form ref={this.form} {...layout} initialValues={this.state}>
                {items}
                <Form.Item className="console-form-toolbar" label="toolbar">{this.toolbar()}</Form.Item>
            </Form>
        );
    }

    readonly = prop => {
        let value = this.state[prop.name];
        if (prop.type === 'read-only:money')
            return toMoney(value, prop.empty);

        if (prop.type === 'read-only:percent')
            return toPercent(value);

        if (prop.type === 'read-only:image') {
            return <Image name={prop.name} upload={prop.upload} size={prop.size || 1} readonly={true} value={this.state[prop.name] || ''} form={this} />;
        }

        if (prop.type === 'read-only:file') {
            let files = [];
            try {
                for (let file of toArray(value)) {
                    files.push(<div key={'file-' + files.length} className="console-form-file">
                        <PaperClipOutlined />
                        <a href={url(file.uri)} target="_blank" rel="noopener noreferrer">{file.name}</a>
                    </div>);
                }
            } catch (e) { }

            return files;
        }

        if (prop.labels)
            return prop.labels[value] || '';

        if (value === 0)
            return 0;

        return value || '';
    }

    input = prop => {
        if (prop.labels) {
            if (prop.labels.length < 5) {
                let radios = [];
                for (let index in prop.labels) {
                    radios.push(<Radio key={index} value={index}>{prop.labels[index]}</Radio>);
                }

                return <Radio.Group>{radios}</Radio.Group>;
            }

            let options = [];
            for (let index in prop.labels) {
                options.push(<Select.Option key={index} value={index}>{prop.labels[index]}</Select.Option>);
            }

            return <Select>{options}</Select>;
        }

        if (prop.type === 'date') return <DatePicker />;

        if (prop.type === 'datetime') return <DatePicker showTime={true} />;

        if (prop.type === 'switch') return <Switch disabled={!prop.service} onChange={this.switch.bind(this, prop)} />;

        if (prop.type === 'text-area') return <Input.TextArea />;

        if (prop.type === 'password') return <Input.Password />;

        return <Input />
    }

    switch = (prop, check) => {
        this.value(prop.name, check ? 1 : 0);
    }

    toolbar = () => {
        let buttons = [];
        if (!this.props.meta.toolbar || this.props.meta.toolbar.length <= 0)
            return buttons;

        let one = this.props.meta.toolbar.length === 1;
        if (this.props.meta.toolbar) {
            for (let toolbar of this.props.meta.toolbar) {
                let button = {
                    key: toolbar.label
                };
                if (one) {
                    button.type = 'primary';
                    button.htmlType = 'submit';
                }
                buttons.push(<Button {...button} onClick={this.button.bind(this, toolbar)}>{toolbar.label}</Button>);
                if (toolbar.success && one)
                    buttons.push(<Button key="cancel" type="dashed" onClick={this.cancel.bind(this, toolbar)}>取消</Button>);
            }
        }

        return buttons;
    }
}

class Normal extends Base {
    load = () => service(this.props.body.uri(this.props.uri, this.props.meta.service), this.props.parameter);

    submit = (mt, values) => service(this.props.body.uri(this.props.uri, mt.service || mt.type), { ...values, ...this.props.parameter });
}

export default Normal;

export { Base };