const toArray = text => {
    if (!text)
        return [];

    try {
        let array = JSON.parse(text);

        return array instanceof Array ? array : [];
    } catch (e) {
        return [];
    }
}

export {
    toArray
}