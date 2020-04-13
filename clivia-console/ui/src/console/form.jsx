import React from 'react';
import { Form, Radio, Select, DatePicker, Input, Button, message } from 'antd';
import moment from 'moment';
import { service } from '../http';
import meta from './meta';
import { toMoney, fromMoney } from './numeric';
import Image from './image';
import Editor from './editor';
import './form.css';

const { Option } = Select;
const { TextArea } = Input;
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

                let values = this.form.current.getFieldsValue();
                for (let key in values) {
                    values[key] = data[key];
                }
                this.format(values);
                this.form.current.setFieldsValue(values);
                this.setState(data);
            });
        }
    }

    format = (values) => {
        for (let prop of meta.props(this.props.props, this.props.meta.props)) {
            let value = values[prop.name];
            if (!value) continue;

            if (prop.labels)
                values[prop.name] = '' + value;
            else if (prop.type === 'date')
                values[prop.name] = moment(value, 'YYYY-MM-DD');
            else if (prop.type === 'datetime')
                values[prop.name] = moment(value, 'YYYY-MM-DD HH:mm:ss');
            else if (prop.type === 'money') {
                values[prop.name] = toMoney(value);
            }
        }
    }

    value = (name, value) => this.values[name] = value;

    button = mt => {
        let values = this.form.current.getFieldsValue();
        for (let prop of meta.props(this.props.props, this.props.meta.props)) {
            let value = values[prop.name];
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
        }
        if (this.props.data)
            for (let key in this.props.data)
                if (!(key in values))
                    values[key] = this.props.data[key];
        this.submit(mt, { ...values, ...this.values }).then(data => {
            if (data === null) return;

            if (mt.success)
                this.props.body.load(this.props.body.uri(this.props.uri, mt.success), this.props.parameter);
            else
                message.success('操作已完成！');
        });
    }

    cancel = mt => {
        this.props.body.load(this.props.body.uri(this.props.uri, mt.success), this.props.parameter);
    }

    render = () => {
        let items = [];
        for (let prop of meta.props(this.props.props, this.props.meta.props)) {
            let item = {
                key: prop.name,
                className: 'console-form-item console-form-item-' + (items.length % 2 === 0 ? 'even' : 'odd'),
                label: prop.label
            };
            if (prop.type.startsWith('read-only')) {
                items.push(<Form.Item {...item}>{this.readonly(prop)}</Form.Item>);
            } else if (prop.type === 'image') {
                items.push(<Form.Item {...item}><Image name={prop.name} upload={prop.upload} size={prop.size || 1} value={this.state[prop.name] || ''} form={this} /></Form.Item>);
            } else if (prop.type === 'editor') {
                items.push(<Form.Item {...item}><Editor name={prop.name} value={this.state[prop.name] || ''} form={this} /></Form.Item>);
            } else {
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
        if (prop.type === 'read-only:money') {
            return toMoney(value);
        }

        if (prop.labels) {
            return prop.labels[value] || '';
        }

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
                options.push(<Option key={index} value={index}>{prop.labels[index]}</Option>);
            }

            return <Select>{options}</Select>
        }

        if (prop.type === 'date') return <DatePicker />;

        if (prop.type === 'datetime') return <DatePicker showTime={true} />;

        if (prop.type === 'text-area') return <TextArea />;

        if (prop.type === 'password') return <Input.Password />;

        return <Input />
    }

    toolbar = () => {
        let buttons = [];
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