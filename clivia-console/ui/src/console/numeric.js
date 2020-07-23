const toMoney = function (value, empty) {
    if (!value) return empty || '';
    
    try {
        return (parseInt(value) / 100).toFixed(2);
    } catch (e) {
        return '0.00';
    }
}

const fromMoney = function (value) {
    try {
        return Math.round(parseFloat(value) * 100);
    } catch (e) {
        return 0;
    }
}

const toPercent = function (value) {
    if (!value) return '0.00 %';

    try {
        return (parseInt(value) / 100).toFixed(2) + ' %';
    } catch (e) {
        return '0.00 %';
    }
}

const fromPercent = function (value) {
    if (!value) return 0;

    try {
        value = value.trim();
        let length = value.length;
        if (length > 1 && value.substring(length - 1) === '%')
            value = value.substring(0, length - 1).trim();

        return Math.round(parseFloat(value) * 100);
    } catch (e) {
        return 0;
    }
}

export {
    toMoney,
    fromMoney,
    toPercent,
    fromPercent
};