import React from 'react';
import { Layout, message } from 'antd';
import { service, url } from '../http';
import Menu from './menu';
import Sign from './sign';
import body from './body';
import './index.css';

class Console extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      body: <div />,
      ringtones: [],
    };
    body.setIndex(this);
    this.ringtones = [];
    service('/console/ringtone').then(data => {
      if (!data || data.length === 0)
        return;

      this.ringtones = data;
      setInterval(this.ringtone, 1000);
    });
  }

  ringtone = () => {
    let time = Math.floor(new Date().getTime() / 1000);
    for (let i = 0; i < this.ringtones.length; i++) {
      let ringtone = this.ringtones[i];
      if (ringtone.second > 1 && time % ringtone.second > 0)
        continue;

      service(ringtone.uri).then(data => {
        if (data === '')
          return;

        message.info(data);
      });
    }
  }

  render = () => (
    <Layout style={{ minHeight: '100vh' }}>
      <Layout.Sider>
        <div className="console-logo">{this.props.logo ? [<img key="img" src={url(this.props.logo)} alt="" />, <div key="div"></div>] : null}</div>
        <div className="console-menu"><Menu /></div>
        <div className="console-copyright">clivia-console &copy; {new Date().getFullYear()}</div>
      </Layout.Sider>
      <Layout>
        <Layout.Header className="console-header">
          <Sign user={this.props.user} body={body} />
        </Layout.Header>
        <Layout.Content>
          <div className="console-body">{this.state.body}</div>
        </Layout.Content>
      </Layout>
    </Layout>
  );
}

export default Console;