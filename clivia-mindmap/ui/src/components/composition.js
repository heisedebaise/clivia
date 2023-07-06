const data = {
    composition: false,
};

const compositionStart = (e) => {
    data.composition = true;
};

const compositionEnd = (e) => {
    data.composition = false;
};

const isComposition = () => data.composition;

export {
    compositionStart,
    compositionEnd,
    isComposition,
}