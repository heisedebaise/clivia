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
        service('/console/menu').then(data => {
            if (data === null || data.length === 0) return;

            let item = data[0].service || data[0].page;
            this.setState({
                items: data,
                item: item
            }, () => this.load(item ? data[0] : data[0].items[0]));
        });
    }

    click = e => {
        this.load(this.map[e.key]);
    };

    load = item => {
        body.setSearch({});
        if (item.service)
            body.load(item.service, item.parameter, item.data);
        else if (item.page)
            body.page(item.page, item.parameter, item.data);
    }

    render = () => {
        if (this.state.items.length === 0) return null;

        return <Menu onClick={this.click} mode="inline" theme="dark" defaultOpenKeys={['0-0']} defaultSelectedKeys={[this.state.item ? '0-0' : '0-0-0']}>{this.menu(this.state.items, '0')}</Menu>;
    }

    menu = (items, parent) => {
        let menus = [];
        if (items.length === 0) return menus;

        for (let i = 0; i < items.length; i++) {
            let key = parent + '-' + i;
            let item = items[i];
            if (item.service || !item.items) {
                this.map[key] = item;
                menus.push(<Menu.Item key={key}>{item.label}</Menu.Item>);
            }
            else {
                menus.push(<SubMenu key={key} title={<span>{item.label}</span>} >{this.menu(item.items, key)}</SubMenu>);
            }
        }

        return menus;
    };
}

export default LeftMenu;