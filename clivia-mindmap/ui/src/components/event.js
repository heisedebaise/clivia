const data = {};

const listen = (type, listener) => {
    let listeners = data[type] || [];
    listeners.push(listener);
    data[type] = listeners;
};

const trigger = (type, event) => {
    let listeners = data[type] || [];
    for (let listener of listeners)
        listener(event);
};

export {
    listen,
    trigger,
};