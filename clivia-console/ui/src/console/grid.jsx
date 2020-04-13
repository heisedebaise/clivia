import React from 'react';
import { Form, Row, Col, Radio, Select, DatePicker, Input, Button, Table, Divider, Menu, Dropdown, Modal } from 'antd';
import { MinusOutlined } from '@ant-design/icons';
import { service, url } from '../http';
import meta from './meta';
import { toMoney } from './numeric';
import './grid.css';

const { Option } = Select;
const { RangePicker } = DatePicker;

class Grid extends React.Component {
    constructor(props) {
        super(props);

        let columns = meta.props(props.props, props.meta.props);
        if (props.meta.search && props.meta.search.length > 0) {
            this.search = <Search key="search" props={meta.props(columns, props.meta.search)} toolbar={props.meta.toolbar} grid={this} />;
        } else if (props.meta.toolbar && props.meta.toolbar.length > 0) {
            let buttons = [];
            for (let toolbar of props.meta.toolbar) {
                buttons.push(this.button(toolbar));
            }
            this.toolbar = <div key="toolbar" className="console-grid-toolbar">{buttons}</div>
        }

        this.columns = [];
        for (let prop of columns) {
            let column = { key: prop.name, title: prop.label };
            if (prop.labels) {
                column.render = model => prop.labels[this.value(model, prop.name)];
            } else if (prop.type === 'money') {
                column.render = model => toMoney(this.value(model, prop.name));
            } else if (prop.type === 'image') {
                column.render = model => {
                    let value = this.value(model, prop.name);

                    return value === '' ? '' : <img src={url(value)} alt="" onClick={this.preview} />;
                }
            } else {
                column.dataIndex = prop.name.split('.');
            }
            this.columns.push(column);
        }
        if (props.meta.ops && props.meta.ops.length > 0) {
            this.columns.push({
                title: '',
                render: model => {
                    let mops = [];
                    for (let op of props.meta.ops)
                        // eslint-disable-next-line
                        if (!op.when || eval(op.when))
                            mops.push(op);

                    let ops = [];
                    if (mops.length <= 2) {
                        for (let op of mops) {
                            if (ops.length > 0) ops.push(<Divider key="divider" type="vertical" />);
                            ops.push(this.action(op, model));
                        }
                    } else {
                        ops.push(this.action(mops[0], model));
                        ops.push(<Divider key="divider" type="vertical" />);
                        let items = [];
                        for (let i = 1; i < mops.length; i++) {
                            items.push(<Menu.Item key={mops[i].label}>{this.action(mops[i], model)}</Menu.Item>);
                        }
                        ops.push(<Dropdown key="more" overlay={<Menu>{items}</Menu>}><span className="console-grid-op">更多</span></Dropdown>);
                    }

                    return <div className="console-grid-ops">{ops}</div>;
                }
            });
        }

        this.state = {
            list: [],
            pagination: false,
            preview: null
        };
        this.load(null);
    }

    value = (model, name) => {
        if (name.indexOf('.') === -1)
            return model[name] || '';

        let m = model;
        for (let n of name.split('.')) {
            if (n in m) {
                m = m[n];
            } else {
                return '';
            }
        }

        return m || '';
    }

    button = op => <Button key={op.label} onClick={this.operate.bind(this, op, {})}>{op.label}</Button>;

    action = (op, model) => <span key={op.label} className="console-grid-op" onClick={this.operate.bind(this, op, model)}>{op.label}</span>;

    operate = (op, model) => {
        if (op.type === 'create') {
            this.props.body.load(this.props.body.uri(this.props.uri, op.service || op.type), this.props.parameter, model && model.id ? { parent: model.id } : {});

            return;
        }

        if (op.type === 'delete' || op.reload) {
            let parameter = { id: model.id };
            if (op.parameter)
                parameter = { ...parameter, ...op.parameter };
            if (this.props.parameter)
                parameter = { ...parameter, ...this.props.parameter };
            service(this.props.body.uri(this.props.uri, op.service || op.type), parameter).then(data => {
                if (data === null) return;

                this.load({ current: this.pageNum || 1 });
            });

            return;
        }

        delete model.children;
        this.props.body.load(this.props.body.uri(this.props.uri, op.service || op.type), this.props.parameter, model);
    }

    preview = e => this.setState({ preview: e.currentTarget.src });

    cancel = () => this.setState({ preview: null });

    load = pagination => {
        let parameter = {};
        if (this.searchValues) {
            parameter = { ...parameter, ...this.searchValues };
        }
        if (pagination) {
            parameter.pageNum = pagination.current;
        }
        if (this.props.parameter) {
            parameter = { ...parameter, ...this.props.parameter };
        }
        service(this.props.uri, parameter).then(data => {
            if (data === null) return;

            if (data instanceof Array) {
                this.setState({
                    list: data
                });
            } else {
                this.pageNum = data.number;
                this.setState({
                    list: data.list,
                    pagination: data.count <= data.size ? false : {
                        total: data.count,
                        pageSize: data.size,
                        current: data.number
                    }
                });
            }
        });
    }

    render = () => {
        let elements = [];
        if (this.search) elements.push(this.search);
        if (this.toolbar) elements.push(this.toolbar);
        elements.push(<Table key="table" columns={this.columns} dataSource={this.state.list} rowKey="id" pagination={this.state.pagination}
            onChange={this.load} className="console-grid" />);
        elements.push(
            <Modal key="preview" visible={this.state.preview != null} footer={null} onCancel={this.cancel}>
                <img style={{ width: '100%' }} src={this.state.preview} alt="" />
            </Modal>
        );

        return elements;
    }
}

class Search extends React.Component {
    render = () => {
        let cols = [];
        for (let column of this.props.props) {
            let item = { label: column.label };
            if (column.type !== 'range')
                item.name = column.name;
            cols.push(
                <Col span={6} key={column.name}>
                    <Form.Item {...item}>{this.input(column)}</Form.Item>
                </Col>
            );
        }
        cols.push(<Col span={2} key="search" className="console-grid-search-btn"><Form.Item><Button type="primary" htmlType="submit">搜索</Button></Form.Item></Col>);
        if (this.props.toolbar && this.props.toolbar.length > 0) {
            for (let toolbar of this.props.toolbar) {
                cols.push(<Col span={2} key={toolbar.label} className="console-grid-search-btn"><Form.Item>{this.props.grid.button(toolbar)}</Form.Item></Col>);
            }
        }

        return (
            <Form className="console-grid-search" onFinish={this.finish}>
                <Row gutter={24}>{cols}</Row>
            </Form>
        );
    }

    input = column => {
        if (column.labels) {
            if (column.labels.length <= 2) {
                let radios = [<Radio key="" value={''}>全部</Radio>];
                for (let index in column.labels) {
                    radios.push(<Radio key={index} value={index}>{column.labels[index]}</Radio>);
                }

                return <Radio.Group initValue={''}>{radios}</Radio.Group>;
            }

            let options = [<Option key={''} value={''}>全部</Option>];
            for (let index in column.labels) {
                options.push(<Option key={index} value={index}>{column.labels[index]}</Option>);
            }

            return <Select>{options}</Select>
        }

        if (column.type === 'date')
            return <DatePicker />;

        if (column.type === 'date-range')
            return <RangePicker />;

        if (column.type === 'range') {
            return (
                <Input.Group className="console-grid-search-range" compact>
                    <Form.Item name={column.name + 'Start'} noStyle><Input /></Form.Item>
                    <span className="range-minus"><MinusOutlined /></span>
                    <Form.Item name={column.name + 'End'} noStyle><Input /></Form.Item>
                </Input.Group>
            );
        }

        return <Input />
    }

    finish = values => {
        console.log(values);
        for (let column of this.props.props) {
            if (column.type === 'range') {
                values[column.name] = (values[column.name + "Start"] || '') + ',' + (values[column.name + "End"] || '');
                delete values[column.name + "Start"];
                delete values[column.name + "End"];

                continue;
            }

            let value = values[column.name];
            console.log(column);
            if (!value) continue;

            if (column.type === 'date') {
                values[column.name] = value.format('YYYY-MM-DD');
            } else if (column.type === 'date-range') {
                if (value.length === 0)
                    values[column.name] = '';
                else
                    values[column.name] = value[0].format('YYYY-MM-DD') + ',' + value[1].format('YYYY-MM-DD')
            }
        }
        this.props.grid.searchValues = values;
        this.props.grid.load(null);
    }
}

export default Grid;