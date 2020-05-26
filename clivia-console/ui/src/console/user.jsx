import React from 'react';

class User extends React.Component {
    render = () => {
        let uids = this.uids();

        return (
            <div className="user">
                {uids.map(uid => <div className="user-uid">{uid}</div>)}
                {this.line(uids, 'nick')}
                {this.line(uids, 'mobile')}
                {this.line(uids, 'email')}
            </div>
        );
    }

    uids = () => {
        if (!this.props.data.auth || this.props.data.auth.length <= 0)
            return [];

        let uids = [];
        for (let i = 0; i < this.props.data.auth.length; i++)
            if (!this.props.data.auth[i].type)
                uids.push(this.props.data.auth[i].uid);

        return uids;
    }

    line = (uids, name) => {
        let value = this.props.data[name];
        if (!value)
            return null;

        for (let uid of uids)
            if (uid === value)
                return null;

        return <div className={'user-' + name}>{value}</div>;
    }
}

export default User;