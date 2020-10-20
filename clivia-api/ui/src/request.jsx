import React from 'react';
import { Space } from 'antd';
import { service } from './http';

class Request extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            request: '',
            response: ''
        };
    }
    componentDidMount = () => {
        service('/api/request').then(data => {
            if (data === null) return;

            this.setState({ request: data });
        });
        service('/api/response').then(data => {
            if (data === null) return;

            this.setState({ response: data });
        });
    }

    render = () => {
        return (
            <Space direction="vertical" style={{ width: '100%' }}>
                <div>参考示例</div>
                <pre>{this.state.request}</pre>
                <div>返回结果</div>
                <pre>{this.state.response}</pre>
            </Space>
        );
    }
}

export default Request;