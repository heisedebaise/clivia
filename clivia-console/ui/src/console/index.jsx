import React from 'react';
import { Layout } from 'antd';
import { url } from '../http';
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