import React from 'react';
import { Space, Alert, Table, Divider } from 'antd';
import { CheckOutlined, CloseOutlined } from '@ant-design/icons';

class Upload extends React.Component {
    responseFile = `{
    "path": "文件URI地址",
    "fileName": "原文件名",
    "fileSize": "文件大小",
    "success": "true-成功；false-失败"
}`;

    responseBase64 = `{
    "code": 0,
    "data": {
        "path": "文件URI地址",
        "fileName": "原文件名",
        "fileSize": "文件大小",
        "success": "true-成功；false-失败"
    }
}`;

    render = () => (
        <Space direction="vertical" style={{ width: '100%' }}>
            <Divider dashed={true}>文件方式上传</Divider>
            <Alert type="info" message={'接口地址：' + this.props.url + '/photon/ctrl-http/upload'} />
            {this.header()}
            <Table title={() => <div className="api-title">参数</div>} columns={[{
                title: '参数名',
                dataIndex: 'name',
                key: 'name',
            }, {
                title: '类型',
                dataIndex: 'type',
                key: 'type',
            }, {
                title: '必须',
                dataIndex: 'require',
                key: 'require',
                render: require => require ? <CheckOutlined /> : <CloseOutlined />
            }, {
                title: '说明',
                dataIndex: 'description',
                key: 'description',
            }]} dataSource={[{ name: this.props.meta.upload, type: 'file', require: true, description: '上传文件。' }]} rowKey="name" pagination={false} />
            <div>返回结果</div>
            <pre>{this.responseFile}</pre>
            <Divider dashed={true}>Base64方式上传</Divider>
            <Alert type="info" message={'接口地址：' + this.props.url + '/photon/ctrl/upload'} />
            {this.header()}
            <Table title={() => <div className="api-title">参数</div>} columns={[{
                title: '参数名',
                dataIndex: 'name',
                key: 'name',
            }, {
                title: '类型',
                dataIndex: 'type',
                key: 'type',
            }, {
                title: '必须',
                dataIndex: 'require',
                key: 'require',
                render: require => require ? <CheckOutlined /> : <CloseOutlined />
            }, {
                title: '说明',
                dataIndex: 'description',
                key: 'description',
            }]} dataSource={[{ name: 'name', type: 'string', require: true, description: '固定为：' + this.props.meta.upload + '。' },
            { name: 'contentType', type: 'string', require: true, description: '文件格式，如：image/jpeg。' },
            { name: 'fileName', type: 'string', require: true, description: '原文件名。' },
            { name: 'base64', type: 'string', require: true, description: 'BASE64编码后的文件内容。' }]} rowKey="name" pagination={false} />
            <div>返回结果</div>
            <pre>{this.responseBase64}</pre>
        </Space>
    );

    header = () => (
        <Table title={() => <div className="api-title">头信息</div>} columns={[{
            title: '头名称',
            dataIndex: 'name',
            key: 'name',
        }, {
            title: '类型',
            dataIndex: 'type',
            key: 'type',
        }, {
            title: '必须',
            dataIndex: 'require',
            key: 'require',
            render: require => require ? <CheckOutlined /> : <CloseOutlined />
        }, {
            title: '说明',
            dataIndex: 'description',
            key: 'description',
        }]} dataSource={this.props.meta.headers} rowKey="name" pagination={false} />
    );
}

export default Upload;