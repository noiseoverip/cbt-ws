function onLoad() {
	loadHeader();
	login();
	onLoadPageSpecific();
}

function loadHeader() {
	$("#header").load("/Navigation.html");
}

function linkTestRunResult(testRunId) {
	return location.protocol + '//' + location.hostname + ":" + location.port
			+ "/TestRunResults.html?testRunId=" + testRunId;
}

function setHeaders(request) {
	request.setRequestHeader('username', 'saulius');
	request.setRequestHeader('password', 'win2k');
}

function login() {
	if (getCookieValue("auth") == "") {
		document.cookie="auth=" + prompt("What is your name?") + ":" + md5(prompt("What is your password?"));
	} 
}

function logout() {
	document.cookie = 'auth=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}

function getCookieValue(key)
{
    currentcookie = document.cookie;
    if (currentcookie.length > 0)
    {
        firstidx = currentcookie.indexOf(key + "=");
        if (firstidx != -1)
        {
            firstidx = firstidx + key.length + 1;
            lastidx = currentcookie.indexOf(";",firstidx);
            if (lastidx == -1)
            {
                lastidx = currentcookie.length;
            }
            return unescape(currentcookie.substring(firstidx, lastidx));
        }
    }
    return "";
}
