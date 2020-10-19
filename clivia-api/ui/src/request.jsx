import React from 'react';
import { Space } from 'antd';
import { service } from './http';

class Request extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            demo: ''
        };
    }
    componentDidMount = () => {
        service('/api/request-demo').then(data => {
            if (data === null) return;

            this.setState({ demo: data });
        })
    }

    render = () => {
        return (
            <Space direction="vertical" style={{ width: '100%' }}>
                <div>参考示例</div>
                <pre>{this.state.demo}</pre>
            </Space>
        );
    }
}

export default Request;