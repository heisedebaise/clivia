import React from 'react';
import { Spin, Dropdown, Avatar } from 'antd';
import { LockOutlined, UserOutlined, LogoutOutlined } from '@ant-design/icons';
import { post, url, loader } from '../http';
import './sign.css';

class Sign extends React.Component {
    constructor(props) {
        super(props);

        this.state = { loading: false };
        loader(this);
    }

    sign = () => {
        this.props.body.load('/user/sign', {}, null);
    }

    password = () => {
        this.props.body.load('/user/password', {}, {});
    }

    signOut = () => {
        post('/user/sign-out').then(json => window.location.reload());
    }

    render = () => {
        let nick = this.props.user.nick || 'Clivia UI';

        return (
            <div className="console-sign">
                <div className="console-sign-loading"><Spin spinning={this.state.loading} /></div>
                <Dropdown menu={{ items: this.items() }}>
                    <div className="console-sign-avatar">
                        {this.props.user.avatar ? <Avatar src={url(this.props.user.avatar)} /> : <Avatar>{nick.substring(0, 1)}</Avatar>}
                        <span>{nick}</span>
                    </div>
                </Dropdown>
            </div>
        );
    }

    items = () => [{
        key: '1',
        icon: <UserOutlined />,
        label: <div onClick={this.sign}>个人信息</div>,
    }, {
        key: '2',
        icon: <LockOutlined />,
        label: <div onClick={this.password}>修改密码</div>,
    }, {
        key: '3',
        icon: <LogoutOutlined />,
        label: <div onClick={this.signOut}>退出</div>,
    }];
}

export default Sign;