const newId = () => {
    for (let i = 0; i < 1024; i++) {
        let id = string(16);
        if (!document.querySelector('#' + id))
            return id;
    }

    return string(16);
};

const string = (length) => {
    let string = 'id';
    while (string.length < length)
        string += Math.random().toString(36).substring(2);

    return string.substring(0, length);
};

export {
    newId
};