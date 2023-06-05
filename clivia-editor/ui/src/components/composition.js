const data = {
    composition: false,
};

const compositionStart = (e) => {
    data.composition = true;
    console.log('start');
};

const compositionEnd = (e) => {
    data.composition = false;
    console.log('end');
};

const isComposition = () => data.composition;

export {
    compositionStart,
    compositionEnd,
    isComposition,
}