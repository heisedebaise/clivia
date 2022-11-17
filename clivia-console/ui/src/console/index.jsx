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
      body: <div />
    };
    body.setIndex(this);
    this.ringtones = [];
    this.ringtoneAudio = null;
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
        if (data.message)
          message.info(data.message);
        if (data.audio) {
          if (this.ringtoneAudio) {
            this.ringtoneAudio.src = data.audio;
            this.ringtoneAudio.muted = '';
            this.ringtoneAudio.play();
          } else {
            message.warn('提示铃声未被激活，请点击下鼠标');
          }
        }
      });
    }
  }

  ringtoneAudioInit = () => {
    if (this.ringtoneAudio)
      return;

    this.ringtoneAudio = document.getElementById('ringtone-audio');
    this.ringtoneAudio.play();
  }

  render = () => (
    <Layout style={{ minHeight: '100vh' }} onClick={this.ringtoneAudioInit}>
      <Layout.Sider>
        <div className="console-logo">{this.props.logo ? [<img key="img" src={url(this.props.logo)} alt="" />, <div key="div"></div>] : null}</div>
        <div className="console-menu"><Menu /></div>
        <div className="console-ringtone"><audio id="ringtone-audio" controls="controls" muted="muted" autoPlay={true}></audio></div>
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