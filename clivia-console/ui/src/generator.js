const random = (length) => {
    let str = '';
    while (str.length < length)
        str += Math.random().toString(36).substring(2);
    str = str.substring(0, length);

    return str;
};

export {
    random
}