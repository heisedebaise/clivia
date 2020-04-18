import React from 'react';
import { Layout, Form, Input, Button, message } from 'antd';
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
        if (values.repeat !== values.password) {
            message.warn('重复密码与密码必须相同！');

            return;
        }

        values.type = '';
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
                            {this.state.up ? <Form.Item name="repeat"><Input.Password prefix={<LockOutlined style={{ color: 'rgba(0,0,0,.25)' }} />} placeholder="重复密码" /></Form.Item> : null}
                            <Form.Item><Button type="primary" htmlType="submit" className="sign-in-up-button">{this.state.up ? '提交注册' : '登录'}</Button></Form.Item>
                            <Form.Item>
                                <Button type="link" className="sign-in-up-link" onClick={this.change}>{this.state.up ? '使用已有账户登录' : '注册新账户'}</Button>
                                <span>其他登录方式</span>
                                <WechatOutlined className="sign-in-third" />
                                <AlipayOutlined className="sign-in-third" />
                                <WeiboOutlined className="sign-in-third" />
                                <GithubOutlined className="sign-in-third" />
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