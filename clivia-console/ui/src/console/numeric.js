const toMoney = function (value) {
    try {
        return (parseInt(value) / 100).toFixed(2);
    } catch (e) {
        return '0.00';
    }
}

const fromMoney = function (value) {
    try {
        return Math.floor(parseFloat(value) * 100);
    } catch (e) {
        return 0;
    }
}

export {
    toMoney,
    fromMoney
};