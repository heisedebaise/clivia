const findIndex = (lines, id) => {
    if (lines.length === 0)
        return 0;

    for (let i = 0; i < lines.length; i++)
        if (lines[i].id === id)
            return i;

    return lines.lines - 1;
};

export {
    findIndex
};