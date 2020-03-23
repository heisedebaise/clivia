import React from 'react';
import { Layout, Form, Input, Button } from 'antd';
import { UserOutlined, LockOutlined, GithubOutlined, WechatOutlined, WeiboOutlined, AlipayOutlined } from '@ant-design/icons';
import { service } from '../http';
import './index.css';

const { Footer, Content } = Layout;

class SignIn extends React.Component {
    constructor() {
        super();

        this.state = {
            up: false
        };
    }

    finish = values => {
        service('/user/sign-' + (this.state.up ? 'up' : 'in'), values).then(data => {
            if (data != null) {
                window.location.reload();
            }
        });
    };

    change = () => {
        this.setState({ up: !this.state.up });
    }

    render() {
        return (
            <Layout style={{ minHeight: '100vh' }}>
                <Content>
                    <div className="sign-in-header">{document.title}</div>
                    <div className="sign-in-form">
                        <Form onFinish={this.finish}>
                            <Form.Item name="uid"><Input prefix={<UserOutlined style={{ color: 'rgba(0,0,0,.25)' }} />} placeholder="用户名" /></Form.Item>
                            <Form.Item name="password"><Input.Password prefix={<LockOutlined style={{ color: 'rgba(0,0,0,.25)' }} />} placeholder="密码" /></Form.Item>
                            <Form.Item>
                                <Button type="primary" htmlType="submit" className="sign-in-button">{this.state.up ? '注册' : '登入'}</Button>
                            </Form.Item>
                            <Form.Item>
                                <Button className="sign-in-button" onClick={this.change}>{this.state.up ? '登录' : '注册'}</Button>
                            </Form.Item>
                            <Form.Item className="sign-in-third">
                                <WechatOutlined />
                                <AlipayOutlined />
                                <WeiboOutlined />
                                <GithubOutlined />
                            </Form.Item>
                        </Form>
                    </div>
                </Content>
                <Footer className="sign-in-footer">Copyright &copy; {new Date().getFullYear()} clivia-console</Footer>
            </Layout>
        );
    }
}

export default SignIn;