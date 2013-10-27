directory.DeviceShare = Backbone.Model.extend({
   
});

directory.DeviceShareList = Backbone.Collection.extend({
   model: directory.DeviceShare,   

   initialize: function(models, options) {
   		this.deviceId = options.deviceId
   },

   url: function () {    	
    	return CbtClient.getDeviceSharing(this.deviceId);;
   }
});