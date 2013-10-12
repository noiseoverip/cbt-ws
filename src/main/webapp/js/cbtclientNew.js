function printDate(time) {
	var date = new Date(time);
	return date.toLocaleDateString() + " " + date.toLocaleTimeString();
}

var CbtClient = {	
	
	cbtRipUrl : "http://127.0.0.1:9090/rip",

	// User should be set after authentication
	userId: 1,

	getUserDevices: function() {		
		$.ajax({
			type : 'GET',
			url : CbtClient.parseUrl('/user/{userId}/device'),
			dataType : "json",
			success : function(json) {
				return json;
			}
		});
	},

	getUserDevicesUrl: function() {
		return CbtClient.parseUrl('/user/{userId}/device');
	},
	
	getUserTestConfigsUrl: function() {
		return CbtClient.parseUrl('/testconfig');
	},

	getUserTestRunsUrl: function() {
		return CbtClient.parseUrl('/testrun');
	},

	getUserTestProfileUrl: function() {
		return CbtClient.parseUrl('/testprofile');
	},

	getUserTestScriptsUrl: function() {
		return CbtClient.parseUrl('/testscript');
	},

	getUserTestTargetsUrl: function() {
		return CbtClient.parseUrl('/testtarget');
	},

	getDeviceTypesUrl: function() {
		return CbtClient.parseUrl('/public/device-types');
	},

	getUserTestScriptUrl: function() {
		return CbtClient.parseUrl('/testscript');
	},
	
	getUserTestTargetUrl: function() {
		return CbtClient.parseUrl('/testtarget');
	},
	
	createNewTestConfig: function(testProfileData, testConfigData, callback) {
		// This is weird function since actual test configuration is 
		// split into test profile and test configuration
		// tetProfileData fields: mode, deviceTypes (array of id's)
		// testConfigData fields: name, testProfileData, testScriptData, testTargetData		

		var createTestConfigutation = function(testConfigData) {
    		$.ajax({
		        type: 'PUT',
		        contentType: 'application/json',
		        url: CbtClient.getUserTestConfigsUrl(),
		        dataType: "json",
		        data: JSON.stringify(testConfigData),
		        success: function(data, textStatus, jqXHR){
		            callback(true);
		        },
		        error: function(jqXHR, textStatus, errorThrown){
		            callback(false);
		        }
		    });
    	}

    	$.ajax({
    			createTestConfigutation : createTestConfigutation,
    			testConfigData : testConfigData,
		        type: 'PUT',
		        contentType: 'application/json',
		        url: CbtClient.getUserTestProfileUrl(),
		        dataType: "json",
		        data: JSON.stringify(testProfileData),
		        success: function(data, textStatus, jqXHR){		            
		            this.testConfigData.testProfileId = data.id;		            
		            createTestConfigutation(this.testConfigData);
		        },
		        error: function(jqXHR, textStatus, errorThrown){
		            alert('Error: ' + textStatus);
		        }
		    });
	},

	parseUrl: function(url) {
		return this.cbtRipUrl + url.replace("{userId}", this.userId);
	}
}

function ConvertFormToJSON(form){
    var array = jQuery(form).serializeArray();
    var json = {};
    
    jQuery.each(array, function() {
        json[this.name] = this.value || '';
    });
    
    return json;
}