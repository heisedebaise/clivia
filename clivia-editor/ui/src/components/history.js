const history = {
    list: [],
    index: 0,
    listener: () => { },
};

const historyListener = (listener) => {
    history.listener = listener;
};

const historyPut = (lines) => {
    if (history.index < history.list.length - 1)
        history.list.splice(history.index + 1, history.list.length);

    let array = [];
    for (let line of lines)
        array.push(line);
    history.list.push(array);

    if (history.list.length > 1024)
        history.list.splice(0, history.list.length - 1024);
    history.index = history.list.length - 1;
    history.listener(history.index > 0, history.index < history.list.length - 1);
};

const historyBack = () => {
    if (history.index <= 0)
        return null;

    history.index--;
    history.listener(history.index > 0, history.index < history.list.length - 1);

    return history.list[history.index];
};

const historyForward = () => {
    if (history.index >= history.list.length - 1)
        return null;

    history.index++;
    history.listener(history.index > 0, history.index < history.list.length - 1);

    return history.list[history.index];
};

export {
    historyListener,
    historyPut,
    historyBack,
    historyForward,
};