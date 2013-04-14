var currentPageParams;
var debug = $("#debug");
var CMS = new ContentManager("hhh");

function ContentManager(username) {  
    this.onLoad = function() {
	//$("#header").load("/Navigation.html"); // load header contents
	$( "#menu" ).menu();	
	this.openPage("devices");
    };
    
    this.getUser = function() {
	return parseInt($.cookie("userId"));
    };
    
    this.setUser = function(user) {
	console.log("Setting user to:" + user);
	return $.cookie("userId", user.id);
    };

    this.openPage = function(pageName, params) {
	if (!this.authenticated() && pageName != "login" && pageName != "register" ) {
	    this.openPage("login", pageName);
	    return;
	}
	console.log("Opening page >> " + pageName + " params:" + params);
	$.ajax({
	    type : 'GET',
	    url : rootURL + '/' + pageName + ".html",
	    dataType : "html",
	    success : function(htmlcontent) {
		$("#contents").empty();
		$("#contents").append(htmlcontent);
		$("#contents").show();
		onLoadPageSpecific(params);
		$("#debug").empty();
		//$("#debug").append("Params:" + params);
		//this.user="dddd";
		$("#debug").append("Current user:" + CMS.getUser());
	    },
	    error : function() {
		alert("Page not found");
	    }
	});
    };
    
    this.authenticated = function() {
	if (getCookieValue("auth") == "") {
	    console.log("unauthorized, sending to login page");	   
	    return false;
	}
	return true;
    };

    this.logOut = function() {
	document.cookie = 'auth=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    };
}

function setHeaders(request) {
    request.setRequestHeader('username', 'saulius');
    request.setRequestHeader('password', 'win2k');
}

function getCookieValue(key) {
    currentcookie = document.cookie;
    if (currentcookie.length > 0) {
	firstidx = currentcookie.indexOf(key + "=");
	if (firstidx != -1) {
	    firstidx = firstidx + key.length + 1;
	    lastidx = currentcookie.indexOf(";", firstidx);
	    if (lastidx == -1) {
		lastidx = currentcookie.length;
	    }
	    return unescape(currentcookie.substring(firstidx, lastidx));
	}
    }
    return "";
}
