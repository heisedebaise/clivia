const data = {};

const setDirection = (direction) => data.direction = direction;

const direction = () => {
    if (data.direction)
        data.direction();
};

export {
    setDirection,
    direction
}