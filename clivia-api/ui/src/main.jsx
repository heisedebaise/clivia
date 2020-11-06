import React from 'react';
import { ConfigProvider, Layout, Menu, Space, Alert, Table, Empty } from 'antd';
import { CheckOutlined, CloseOutlined } from '@ant-design/icons';
import zhCN from 'antd/es/locale/zh_CN';
import { service, url } from './http';
import Request from './request';
import Upload from './upload';
import './main.css';

class Main extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            logo: '',
            user: {},
            data: [],
            item: {}
        };
        this.map = {};
        this.url = window.location.href;
        this.url = this.url.substring(0, this.url.length - window.location.pathname.length);
    }

    componentDidMount = () => {
        service('/keyvalue/object', { key: 'setting.global.' }).then(data => {
            if (data === null) return;

            document.title = data['setting.global.console.title'] || 'Clivia API';
            this.setState({ logo: data['setting.global.console.logo'] });
        });
        service('/user/sign').then(data => this.setState({ user: data }));
        service('/api/get').then(data => {
            if (data === null) return;

            for (let module of data) {
                for (let child of module.children) {
                    if (child.psid) {
                        if (!child.headers) child.headers = [];
                        child.headers.push({
                            name: 'photon-session-id',
                            type: 'string',
                            require: true,
                            description: '用户SESSION ID值。'
                        });
                    }
                    if (module.model) {
                        if (child.response === 'model')
                            child.response = module.model;
                        else if (child.response === 'pagination') {
                            if (!child.parameters) child.parameters = [];
                            child.parameters.push({ name: 'pageSize', type: 'int', description: '每页显示记录数，默认：20。' });
                            child.parameters.push({ name: 'pageNum', type: 'int', description: '当前显示页数。' });
                            child.response = `{
    "count":"记录总数。",
    "size":"每页最大显示记录数。",
    "number":"当前显示页数。",
    "page":"总页数。",
    "list":[
        `+ module.model.replace(/\n {4}/g, '\n            ').replace('\n}', '\n        }') + `
    ]
}`;
                        }
                    }
                }
            }
            let list = [{
                name: '通用',
                children: [{
                    name: 'HTTP请求',
                    page: 'request'
                }, {
                    name: '参数签名',
                    page: 'sign'
                }]
            }];
            for (let d of data)
                list.push(d);
            this.setState({ data: list }, () => this.show({ key: '0-0-0' }));
        });
    }

    render = () => (
        <ConfigProvider locale={zhCN}>
            <Layout style={{ minHeight: '100vh' }}>
                <Layout.Sider>
                    <div className="api-logo">{this.props.logo ? [<img key="img" src={url(this.props.logo)} alt="" />, <div key="div"></div>] : null}</div>
                    <div className="api-menu"><Menu onClick={this.show} mode="inline" theme="dark" defaultOpenKeys={['0-0']} defaultSelectedKeys={[this.state.item.uri ? '0-0' : '0-0-0']}>{this.menu(this.state.data, '0')}</Menu></div>
                    <div className="api-copyright">clivia-api &copy; {new Date().getFullYear()}</div>
                </Layout.Sider>
                <Layout>
                    <Layout.Header className="api-header">
                    </Layout.Header>
                    <Layout.Content>
                        <div className="api-body">{this.body()}</div>
                    </Layout.Content>
                </Layout>
            </Layout>
        </ConfigProvider>
    );

    menu = (items, parent) => {
        let menus = [];
        if (items.length === 0) return menus;

        for (let i = 0; i < items.length; i++) {
            let key = parent + '-' + i;
            let item = items[i];
            if (item.children)
                menus.push(<Menu.SubMenu key={key} title={<span>{item.name}</span>} >{this.menu(item.children, key)}</Menu.SubMenu>);
            else {
                this.map[key] = item;
                menus.push(<Menu.Item key={key}>{item.name}</Menu.Item>);
            }
        }

        return menus;
    };

    show = e => {
        this.setState({ item: this.map[e.key] });
    }

    body = () => {
        if (this.state.item.page) {
            switch (this.state.item.page) {
                case 'request':
                    return <Request />;
                case 'upload':
                    return <Upload url={this.url} meta={this.state.item} />;
                default:
                    return <div />;
            }
        }

        return (
            <Space direction="vertical" style={{ width: '100%' }}>
                <Alert type="info" message={'接口地址：' + this.url + this.state.item.uri} />
                <Table title={() => <div className="api-title">头信息</div>} columns={[{
                    title: '头名称',
                    dataIndex: 'name',
                    key: 'name',
                }, {
                    title: '类型',
                    dataIndex: 'type',
                    key: 'type',
                }, {
                    title: '必须',
                    dataIndex: 'require',
                    key: 'require',
                    render: require => require ? <CheckOutlined /> : <CloseOutlined />
                }, {
                    title: '说明',
                    dataIndex: 'description',
                    key: 'description',
                }]} dataSource={this.state.item.headers} rowKey="name" pagination={false} locale={{ emptyText: <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description='无需头信息' /> }} />
                <Table title={() => <div className="api-title">参数</div>} columns={[{
                    title: '参数名',
                    dataIndex: 'name',
                    key: 'name',
                }, {
                    title: '类型',
                    dataIndex: 'type',
                    key: 'type',
                }, {
                    title: '必须',
                    dataIndex: 'require',
                    key: 'require',
                    render: require => require ? <CheckOutlined /> : <CloseOutlined />
                }, {
                    title: '说明',
                    dataIndex: 'description',
                    key: 'description',
                }]} dataSource={this.state.item.parameters} rowKey="name" pagination={false} locale={{ emptyText: <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description='无需参数' /> }} />
                <div className="api-response-title">返回</div>
                <pre>{this.state.item.response}</pre>
            </Space>
        );
    }
}

export default Main;