import React from 'react';
import { Space, Alert, Table } from 'antd';
import { CheckOutlined, CloseOutlined } from '@ant-design/icons';

class Setting extends React.Component {
    render = () => (
        <Space direction="vertical" style={{ width: '100%' }}>
            <Alert type="info" message={'接口地址：' + this.props.url + '/keyvalue/object'} />
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
            }]} dataSource={[{
                name: 'photon-session-id',
                type: 'string',
                require: true,
                description: '用户SESSION ID值，如：' + localStorage.getItem('photon-session-id') + '。'
            }]} rowKey="name" pagination={false} />
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
            }]} dataSource={[{ name: 'key', type: 'string', require: true, description: '固定为：【' + this.props.meta.prefix + '】。' }]} rowKey="name" pagination={false} />
            <div>返回结果</div>
            <pre>{'{\n' + this.props.meta.keys.map(kd => '    "' + kd.key + '":"' + kd.description + '",\n').join('') + '}'}</pre>
        </Space>
    );
}

export default Setting;