const root = 'http://192.168.20.114:8080';

const service = (uri, data, success) => {
    post(uri, data, json => {
        if (json.code === 0 && success) {
            success(json.data);
        }
    });
}

const post = (uri, body, success) => {
    let header = {};
    psid(header);
    fetch(root + uri, {
        method: 'POST',
        headers: header,
        body: JSON.stringify(body)
    }).then(response => {
        if (uri === '/user/sign-out')
            localStorage.removeItem('photon-session-id');

        if (response.ok && success)
            response.json().then(success);
    });
}

const upload = (name, file, fileName, success, progress) => {
    let xhr = new XMLHttpRequest();
    xhr.upload.addEventListener('progress', event => {
        if (progress)
            progress(Math.round(100 * event.loaded / event.total));
    });
    xhr.addEventListener('load', event => {
        if (success)
            success(JSON.parse(xhr.responseText));
    });
    xhr.open('POST', root + '/photon/ctrl-http/upload');
    let header = {};
    psid(header);
    for (let key in header)
        xhr.setRequestHeader(key, header[key]);
    let body = new FormData();
    if (fileName)
        body.append(name, file, fileName);
    else
        body.append(name, file);
    xhr.send(body);
}

const psid = (header) => {
    // localStorage.setItem('photon-session-id','1s5q29jh9oe3sqbig4j9qtijlid1ellr9qv6tsseqjpo1lws3cidhhalkqpmcv5q');
    let psid = localStorage.getItem('photon-session-id');
    if (!psid) {
        psid = '';
        while (psid.length < 64) psid += Math.random().toString(36).substring(2);
        psid = psid.substring(0, 64);
        localStorage.setItem('photon-session-id', psid);
    }
    header['photon-session-id'] = psid;
}

const url = uri => {
    if (!uri)
        return null;

    if (uri.indexOf('://') > -1)
        return uri;

    return root + uri;
}

export {
    post,
    service,
    upload,
    url,
}