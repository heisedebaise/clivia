import React from 'react';
import { Layout } from 'antd';
import { url } from '../http';
import Menu from './menu';
import Sign from './sign';
import body from './body';
import './index.css';

const { Header, Footer, Sider, Content } = Layout;

class Console extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      body: <div />
    };
    body.setIndex(this);
  }

  componentDidMount = () => {
    this.setState({
      body: <div />
    });
  }

  render = () => (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider>
        <div className="console-logo">{this.props.logo ? [<img key="img" src={url(this.props.logo)} alt="" />, <div key="div"></div>] : null}</div>
        <Menu />
      </Sider>
      <Layout>
        <Header className="console-header">
          <Sign user={this.props.user} body={body} />
        </Header>
        <Content>
          <div className="console-body">{this.state.body}</div>
        </Content>
        <Footer className="console-footer">Copyright &copy; {new Date().getFullYear()} clivia-console</Footer>
      </Layout>
    </Layout>
  );
}

export default Console;