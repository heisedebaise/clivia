import React from 'react';
import { Col, Row, Collapse, List, Avatar, Button } from 'antd';
import { PictureOutlined } from '@ant-design/icons';
import { service, upload, url } from '../http';
import './olcs.css';

class Olcs extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            all: [],
            user: {},
            messages: [],
            time: '',
            timer: true,
        };
        this.timer();
    }

    componentWillUnmount = () => {
        this.state.timer = false;
    }

    timer = () => {
        if (this.state.timer)
            setTimeout(this.timer.bind(this), 1000);
        service('/olcs/member/query').then(data => {
            if (data === null)
                return;

            this.setState({ all: data.all, newer: data.newer });
        });
        if (this.state.user.id) {
            service('/olcs/query', { user: this.state.user.id, time: this.state.time }).then(data => {
                if (data === null || data.length === 0)
                    return;

                let messages = this.state.messages;
                for (let message of data) {
                    messages.push(message);
                }
                this.setState({ messages, time: messages[messages.length - 1].time });
                setTimeout(() => {
                    let msgs = document.querySelector('.olcs-messages');
                    msgs.scrollTop = msgs.scrollHeight;
                }, 250);
            });
        }
    }

    chat = (item) => {
        this.setState({ user: item, messages: [], time: '' });
    }

    avatar = (user) => user.avatar ? <Avatar src={url(user.avatar)} /> : <Avatar>{(user.nick || 'C').substring(0, 1)}</Avatar>;

    message = () => {
        if (!this.state.user.id)
            return <div></div>;

        let messages = [];
        for (let message of this.state.messages) {
            let content = null;
            if (message.genre === 'image') {
                content = (
                    <a href={url(message.content)} target="_blank" >
                        <img src={url(message.content)} />
                    </a>
                );
            } else {
                if (message.content.indexOf('\n') === -1) {
                    content = message.content;
                } else {
                    content = [];
                    for (let line of message.content.split('\n'))
                        content.push(<div key={content.length}>{line}</div>);
                }
            }
            if (message.replier) {
                messages.push(
                    <div className="olcs-message-replier" key={message.id}>
                        <div className="olcs-message-content">
                            <div className={'olcs-message-' + message.genre}>{content}</div>
                            <div className="olcs-message-time">{message.time}</div>
                        </div>
                        {this.avatar(message.replier)}
                    </div>
                );
            } else {
                messages.push(
                    <div className="olcs-message-user" key={message.id}>
                        {this.avatar(this.state.user)}
                        <div className="olcs-message-content">
                            <div className={'olcs-message-' + message.genre}>{content}</div>
                            <div className="olcs-message-time">{message.time}</div>
                        </div>
                    </div>
                );
            }
        }

        return (
            <Col span={18}>
                <div className="olcs-content">
                    <div className="olcs-avatar-nick">
                        {this.avatar(this.state.user)}
                        <span className="nick">{this.state.user.nick || ''}</span>
                    </div>
                    <div className="olcs-messages">{messages}</div>
                    <div className="olcs-tools">
                        <span className="olcs-tool" onClick={this.picture}>
                            <PictureOutlined />
                        </span>
                    </div>
                    <div className="olcs-input">
                        <textarea></textarea>
                    </div>
                    <div className="olcs-send">
                        <Button type="primary" onClick={this.send}>发送</Button>
                    </div>
                </div>
            </Col>
        );
    }

    picture = () => {
        let input = document.createElement('input');
        input.type = 'file';
        input.style.position = 'absolute';
        input.style.top = '-9999px';
        input.accept = 'image/*';
        document.body.appendChild(input);
        input.onchange = (e) => {
            upload('clivia.olcs.image', e.target.files[0]).then(data => {
                document.body.removeChild(input);
                if (data === null)
                    return;

                service('/olcs/reply', { user: this.state.user.id, genre: 'image', content: data.path }).then(d => {
                    if (d === null)
                        return;

                    document.querySelector('.olcs-input textarea').focus();
                });
            });
        };
        input.click();
    }

    send = () => {
        let textarea = document.querySelector('.olcs-input textarea');
        service('/olcs/reply', { user: this.state.user.id, genre: 'text', content: textarea.value }).then(data => {
            if (data === null)
                return;

            textarea.value = '';
            textarea.focus();
        });
    }

    render = () => {
        return (
            <Row gutter={[8, 8]}>
                <Col span={6} className="olcs-member">
                    <Collapse defaultActiveKey={['all']}>
                        <Collapse.Panel key={'all'} header="会员">
                            <List
                                itemLayout="horizontal"
                                dataSource={this.state.all}
                                renderItem={item => (
                                    <List.Item className="olcs-item" onClick={this.chat.bind(this, item)}>
                                        <List.Item.Meta
                                            avatar={this.avatar(item)}
                                            title={item.nick}
                                            description={item.content}
                                        />
                                    </List.Item>
                                )}
                            />
                        </Collapse.Panel>
                        <Collapse.Panel key={'newer'} header="新会员">
                            <List
                                itemLayout="horizontal"
                                dataSource={this.state.newer}
                                renderItem={item => (
                                    <List.Item className="olcs-item" onClick={this.chat.bind(this, item)}>
                                        <List.Item.Meta
                                            avatar={this.avatar(item)}
                                            title={item.nick}
                                            description={item.content}
                                        />
                                    </List.Item>
                                )}
                            />
                        </Collapse.Panel>
                    </Collapse>
                </Col>
                {this.message()}
            </Row>
        );
    }
}

export default Olcs;