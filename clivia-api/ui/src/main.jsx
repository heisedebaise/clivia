import React from 'react';
import { ConfigProvider, Layout, Menu, Space, Alert, Table, Empty } from 'antd';
import { CheckOutlined, CloseOutlined } from '@ant-design/icons';
import zhCN from 'antd/es/locale/zh_CN';
import { service, url } from './http';
import './main.css';

class Main extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            logo: '',
            user: {},
            data: [],
            page: null,
            item: {}
        };
        this.map = {};
        this.url = window.location.href;
        if (this.url.endsWith('/api'))
            this.url = this.url.substring(0, this.url.length - 4);
        else if (this.url.endsWith('/'))
            this.url = this.url.substring(0, this.url.length - 1);
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
                if (module.model) {
                    for (let child of module.children) {
                        if (child.response === 'model')
                            child.response = module.model;
                        else if (child.response === 'page') {
                            child.response = '{\n'
                                + '    "count":"记录总数。",\n'
                                + '    "size":"每页最大显示记录数。",\n'
                                + '    "number":"当前显示页数。",\n'
                                + '    "page":"总页数。",\n'
                                + '    "list":[\n'
                                + '        ' + module.model.replace(/\n {4}/g, '\n            ').replace('\n}', '\n        }')
                                + '\n    ]\n'
                                + '}';
                        }
                    }
                }
            }
            this.setState({ data: data }, () => this.show({ key: '0-0-0' }));
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
        if (this.state.page || !this.state.item.uri) return <div />;

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
                }]} dataSource={this.state.item.headers} pagination={false} locale={{ emptyText: <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description='无需头信息' /> }} />
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
                }]} dataSource={this.state.item.parameters} pagination={false} locale={{ emptyText: <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description='无需参数' /> }} />
                <div className="api-response-title">返回</div>
                <pre className="api-response">{this.state.item.response}</pre>
            </Space>
        );
    }
}

export default Main;