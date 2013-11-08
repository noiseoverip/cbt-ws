directory.DeviceShareUser = Backbone.Model.extend({   
});

directory.DeviceShareUserList = Backbone.Collection.extend({
   model: directory.DeviceShareUser,   

   initialize: function(models, options) {
   		this.deviceId = options.deviceId
   },

   url: function () {    	
    	return CbtClient.getDeviceSharingUsers(this.deviceId);;
   }
});

directory.DeviceShareGroup = Backbone.Model.extend({   
});

directory.DeviceShareGroupList = Backbone.Collection.extend({
   model: directory.DeviceShareGroup,   

   initialize: function(models, options) {
   		this.deviceId = options.deviceId
   },

   url: function () {    	
    	return CbtClient.getDeviceSharingGroups(this.deviceId);;
   }
});