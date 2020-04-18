import React from 'react';
import { Menu } from 'antd';
import { service } from '../http';
import body from './body';

const { SubMenu } = Menu;

class LeftMenu extends React.Component {
    constructor() {
        super();

        this.state = {
            items: []
        };
        this.map = {};
        service('/console/menu').then(data => this.setState({ items: data }));
    }

    click = e => {
        let item = this.map[e.key];
        body.load(item.service, item.parameter, item.data);
    };

    render = () => <Menu onClick={this.click} mode="inline" theme="dark">{this.menu(this.state.items)}</Menu>;

    menu = items => {
        let menus = [];
        if (items.length === 0) return menus;

        for (let item of items) {
            let key = Math.random().toString(36).substring(2);
            if (item.items) {
                menus.push(<SubMenu key={key} title={<span>{item.label}</span>} >{this.menu(item.items)}</SubMenu>);
            } else {
                this.map[key] = item;
                menus.push(<Menu.Item key={key}>{item.label}</Menu.Item>);
            }
        }

        return menus;
    };
}

export default LeftMenu;