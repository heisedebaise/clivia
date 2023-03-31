var on = false;

const compositionstart = (e) => {
    on = true;
};

const composition = () => on;

const compositionend = (e) => {
    on = false;
};

export {
    compositionstart,
    composition,
    compositionend,
};