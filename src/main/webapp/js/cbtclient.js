// This one is used only for locating html pages
var rootURL = "http://127.0.0.1:8080";
var cbtRipUrl = "http://127.0.0.1:8080/rip";

// Parses RIP url requests
function url(url) {
    return cbtRipUrl + url.replace("{userId}", CMS.getUser());
}

function printDate(time) {
	var date = new Date(time);
	return date.toLocaleDateString() + " " + date.toLocaleTimeString();
}