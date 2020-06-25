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

    render = () => <Select showSearch={true} filterOption={this.filter} onChange={this.change} value={this.state.value}>{this.state.list.map(option => <Select.Option key={option[this.props.vname]} value={option[this.props.vname]}>{option[this.props.lname]}</Select.Option>)}</Select>;
}

export default DSelect;