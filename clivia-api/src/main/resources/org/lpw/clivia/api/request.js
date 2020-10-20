const post = (url, body) => fetch(url, {
    method: 'POST',
    headers: header(),
    body: JSON.stringify(body)
}).then(response => {
    if (response.ok) return response.json();

    message.warn('[' + response.status + ']' + response.statusText);

    return null;
});

const header = () => {
    let header = {
        'Content-Type': 'application/json'
    };
    psid(header, true);

    return header;
}

const psid = (header) => {
    let psid = localStorage.getItem('photon-session-id');
    if (!psid) {
        psid = '';
        while (psid.length < 64) psid += Math.random().toString(36).substring(2);
        psid = psid.substring(0, 64);
        localStorage.setItem('photon-session-id', psid);
    }
    header['photon-session-id'] = psid;
}