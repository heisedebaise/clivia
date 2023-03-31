const focus = (e) => {
    let children = e.target.children;
    children[children.length - 1].focus();
};

export {
    focus
};