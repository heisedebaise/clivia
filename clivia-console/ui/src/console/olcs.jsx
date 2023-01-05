import React from 'react';
import { Col, Row, Collapse, List, Avatar, Button } from 'antd';
import { SmileOutlined, PictureOutlined, HistoryOutlined } from '@ant-design/icons';
import { service, url } from '../http';
import './olcs.css';

class Olcs extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            all: [],
            user: {},
        };
        this.timer();
    }

    timer = () => {
        service('/olcs/users').then(data => {
            if (data === null)
                return;

            this.setState({ all: data.all });
        });
    }

    chat = (item) => {
        this.setState({ user: item });
    }

    avatar = (user) => user.avatar ? <Avatar src={url(user.avatar)} /> : <Avatar>{user.nick.substring(0, 1)}</Avatar>;

    render = () => {
        return (
            <Row gutter={[8, 8]}>
                <Col span={6}>
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
                                            description="Ant Design, a design language for background applications, is refined by Ant UED Team"
                                        />
                                    </List.Item>
                                )}
                            />
                        </Collapse.Panel>
                        <Collapse.Panel key={'newer'} header="新会员">newer</Collapse.Panel>
                    </Collapse>
                </Col>
                {this.state.user.id ? <Col span={18}>
                    <div className="olcs-content">
                        <div className="olcs-avatar-nick">
                            {this.avatar(this.state.user)}
                            <span className="nick">{this.state.user.nick || ''}</span>
                        </div>
                        <div className="olcs-messages"></div>
                        <div className="olcs-tools">
                            <span className="olcs-tool">
                                <SmileOutlined />
                            </span>
                            <span className="olcs-tool">
                                <PictureOutlined />
                            </span>
                        </div>
                        <div className="olcs-input">
                            <textarea></textarea>
                        </div>
                        <div className="olcs-send">
                            <Button type="primary">发送</Button>
                        </div>
                    </div>
                </Col> : null}

            </Row>
        );
    }
}

export default Olcs;