import { store } from "../store";

const history = {
    list: [],
    index: 0,
};

const historyPut = () => {
    let string = JSON.stringify(store.lines);
    if (string === history.list[history.index])
        return;

    if (history.index < history.list.length - 1)
        history.list.splice(history.index + 1, history.list.length);
    history.list.push(string);
    if (history.list.length > 1024)
        history.list.splice(0, history.list.length - 1024);
    history.index = history.list.length - 1;
};

const historyEnable = (back) => {
    if (back)
        return history.index > 0;

    return history.index < history.list.length - 1;
};

const historyBack = () => {
    if (history.index <= 0)
        return;

    history.index--;
    store.lines = JSON.parse(history.list[history.index]);
};

const historyForward = () => {
    if (history.index >= history.list.length - 1)
        return;

    history.index++;
    store.lines = JSON.parse(history.list[history.index]);
};

export {
    historyPut,
    historyEnable,
    historyBack,
    historyForward,
};