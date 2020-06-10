import React from 'react';
import { TreeSelect } from 'antd';
import { service } from '../http';

class Category extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            data: []
        };
        service('/category/list', { key: props.list }).then(data => {
            if (data === null)
                return;

            let list = [];
            this.format(list, data);
            this.setState({ data: list });
        });
    }

    format = (target, source) => {
        console.log(source);
        if (source.length === 0)
            return;

        for (let category of source) {
            let element = {
                title: category.name,
                value: category.id
            };
            if (category.children) {
                element.children = [];
                this.format(element.children, category.children);
            }
            target.push(element);
        }
    }

    render = () => <TreeSelect dropdownStyle={{ maxHeight: 400, overflow: 'auto' }} treeData={this.state.data} defaultValue="74fd6625-307e-455d-b0b7-192d7beadd28" />;
}

export default Category;