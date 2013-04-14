var rootURL = "http://127.0.0.1:8081";

function url(url) {
    return rootURL + url.replace("{userId}", CMS.getUser());
}

function printDate(time) {
	var date = new Date(time);
	return date.toLocaleDateString() + " " + date.toLocaleTimeString();
}