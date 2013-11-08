function printDate(time) {
   "use strict";
   var date = new Date(time);
   return date.toLocaleDateString() + " " + date.toLocaleTimeString();
}

var CbtClient = {

   cbtRipUrl: "http://127.0.0.1:8080/rip",

   // User should be set after authentication
   userId: 2,

   getUserDevices: function () {
      "use strict";
      $.ajax({
         type: 'GET',
         url: CbtClient.parseUrl('/user/{userId}/device'),
         dataType: "json",
         success: function (json) {
            return json;
         }
      });
   },

   getUserDevicesUrl: function () {
      "use strict";
      return CbtClient.parseUrl('/device');
   },

   getUserTestConfigUrl: function () {
      "use strict";
      return CbtClient.parseUrl('/testconfig');
   },

   getUserTestRunsUrl: function () {
      "use strict";
      return CbtClient.parseUrl('/testrun');
   },  

   getDeviceSharingUsers: function (id) {
      "use strict";
      return CbtClient.parseUrl('/device/' + id + "/share/user");
   },

   getDeviceSharingGroups: function (id) {
      "use strict";
      return CbtClient.parseUrl('/device/' + id + "/share/group");
   },

   getUserTestProfileUrl: function () {
      "use strict";
      return CbtClient.parseUrl('/testprofile');
   },

   getDeviceTypesUrl: function () {
      "use strict";
      return CbtClient.parseUrl('/public/device-types');
   },

   getUserTestScriptUrl: function () {
      "use strict";
      return CbtClient.parseUrl('/testscript');
   },

   getUserTestTargetUrl: function () {
      "use strict";
      return CbtClient.parseUrl('/testtarget');
   },

   getDeviceJobByTestRun: function (testRunId) {
      "use strict";
      return CbtClient.parseUrl('/devicejob?testRunId=' + testRunId);
   },

   getDeviceJobResult: function (deviceJobId) {
      "use strict";
      return CbtClient.parseUrl('/devicejob/' + deviceJobId + '/result');
   },   
   createUser: function(username, password, email, callbackSuccess, callbackFailure) {
      $.ajax({
         type: 'PUT',
         contentType: 'application/json',
         dataType: "json",
         url: CbtClient.parseUrl("/public/user"),
         data: JSON.stringify({username : username, password : password, email : email}),
         success: function (data, textStatus, jqXHR) {
            callbackSuccess();
         },
         error: function (jqXHR, textStatus, errorThrown) {       
            callbackFailure($.parseJSON(jqXHR.responseText)[0]);
         }
      });
   },
   // callback: success, returnData
   createDeviceShareUser : function(deviceId, userToShareWith, callback) {
		$.ajax({
         type: 'PUT',
         url: CbtClient.getDeviceSharingUsers(deviceId),
         data: {username : userToShareWith},
         success: function (data, textStatus, jqXHR) {
            if (callback) {
               callback(true, data);
            }
         },
         error: function (data, textStatus, jqXHR) {
             if (callback) {
               callback(false, data);
            }
         }
      });
   },
   // callback: success, returnData
   createDeviceShareGroup : function(deviceId, groupId, callback) {
      $.ajax({
         type: 'PUT',
         url: CbtClient.getDeviceSharingGroups(deviceId),
         data: {groupId : groupId},
         success: function (data, textStatus, jqXHR) {
            if (callback) {
               callback(true, data);
            }
         },
         error: function (data, textStatus, jqXHR) {
             if (callback) {
               callback(false, data);
            }
         }
      });
   },

   createTestRun: function (testConfigId, callback) {
      "use strict";
      var testRun = {testconfigId: testConfigId};
      $.ajax({
         type: 'PUT',
         contentType: 'application/json',
         url: CbtClient.getUserTestRunsUrl(),
         dataType: "json",
         data: JSON.stringify(testRun),
         success: function (data, textStatus, jqXHR) {
            if (callback) {
               callback(null, data);
            }
         },
         statusCode: {
            412: function () {
               // didn't find any specified devices online
               if (callback) {
                  callback("Could not find any available devices at this time");
               }
            }
         }
      });
   },

   createNewTestConfig: function (testProfileData, testConfigData, callback) {
      // This is weird function since actual test configuration is
      // split into test profile and test configuration
      // tetProfileData fields: mode, deviceTypes (array of id's)
      // testConfigData fields: name, testProfileData, testScriptData, testTargetData

      "use strict";
      var createTestConfigutation = function (testConfigData) {
         $.ajax({
            type: 'PUT',
            contentType: 'application/json',
            url: CbtClient.getUserTestConfigUrl(),
            dataType: "json",
            data: JSON.stringify(testConfigData),
            success: function (data, textStatus, jqXHR) {
               callback(true);
            },
            error: function (jqXHR, textStatus, errorThrown) {
               callback(false);
            }
         });
      };

      $.ajax({
         createTestConfigutation: createTestConfigutation,
         testConfigData: testConfigData,
         type: 'PUT',
         contentType: 'application/json',
         url: CbtClient.getUserTestProfileUrl(),
         dataType: "json",
         data: JSON.stringify(testProfileData),
         success: function (data, textStatus, jqXHR) {
            this.testConfigData.testProfileId = data.id;
            createTestConfigutation(this.testConfigData);
         },
         error: function (jqXHR, textStatus, errorThrown) {
            alert('Error: ' + textStatus);
         }
      });
   },

   parseUrl: function (url) {
      "use strict";
      return this.cbtRipUrl + url.replace("{userId}", this.userId);
   }
};

function ConvertFormToJSON(form) {
   "use strict";
   var array = jQuery(form).serializeArray();
   var json = {};

   jQuery.each(array, function () {
      json[this.name] = this.value || '';
   });

   return json;
}

function getStatusCssClass(state) {
   "use strict";
   var clazz;
   switch (state) {
      case "PASSED":
         clazz = "success";
         break;
      case "FAILED":
         clazz = "danger";
         break;
      case "RUNNING":
         clazz = "primary";
         break;
      default:
         clazz = "default";
         break;
   }
   return clazz;
}