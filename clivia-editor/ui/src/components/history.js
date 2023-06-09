import { store } from "../store";
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
    history.list.push(JSON.stringify(lines));
    if (history.list.length > 1024)
        history.list.splice(0, history.list.length - 1024);
    history.index = history.list.length - 1;
    history.listener(history.index > 0, history.index < history.list.length - 1);
};

const historyBack = () => {
    if (history.index <= 0)
        return;

    history.index--;
    history.listener(history.index > 0, history.index < history.list.length - 1);
    store.lines = JSON.parse(history.list[history.index]);
};

const historyForward = () => {
    if (history.index >= history.list.length - 1)
        return;

    history.index++;
    history.listener(history.index > 0, history.index < history.list.length - 1);
    store.lines = JSON.parse(history.list[history.index]);
};

export {
    historyListener,
    historyPut,
    historyBack,
    historyForward,
};