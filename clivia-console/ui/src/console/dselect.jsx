import React from 'react';
import { Select } from 'antd';
import { service } from '../http';

class DSelect extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            list: [],
            value: ''
        };
        this.vname = props.vname || 'id';
        this.lname = props.lname || 'name';
        props.form.value(props.name, props.value);
        service(props.list, props.parameter).then(data => {
            if (data === null) return;

            this.setState({
                list: data,
                value: props.value
            });
        });
    }

    filter = (input, option) => option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;

    change = value => {
        this.setState({ value: value });
        this.props.form.value(this.props.name, value);
    }

    render = () => <Select showSearch={true} filterOption={this.filter} onChange={this.change} value={this.state.value}>{this.state.list.map(option => <Select.Option key={option[this.vname]} value={option[this.vname]}>{option[this.lname]}</Select.Option>)}</Select>;
}

export default DSelect;