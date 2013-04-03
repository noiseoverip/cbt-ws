var rootURL = "http://127.0.0.1:8081";

function printDate(time) {
	var date = new Date(time);
	return date.toLocaleDateString() + " " + date.toLocaleTimeString();
}