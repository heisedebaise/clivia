import { service } from '../http';
import { Base } from './form';

class Setting extends Base {
    load = () => service('/keyvalue/list', { key: this.key() }).then(data => {
        if (data === null) return null;

        let kvs = {};
        for (let kv of data) {
            kvs[kv.key] = kv.value;
        }

        return kvs;
    });

    submit = (mt, values) => {
        let array = [];
        for (let name in values) {
            array.push({
                key: name,
                value: values[name] || ''
            });
        }

        return service('/keyvalue/saves', { kvs: JSON.stringify(array) });
    }

    key = () => this.props.uri.substring(1).replace(/\//g, '.') + '.';
}

export default Setting;