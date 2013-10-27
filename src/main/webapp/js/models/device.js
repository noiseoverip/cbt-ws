directory.Device = Backbone.Model.extend({
   	parse: function (response) {
      	response.updatedMoment = moment(response.updated);
      	return response;
   	},

      urlRoot: function() {
         return CbtClient.getUserDevicesUrl();
      }
});

directory.DeviceList = Backbone.Collection.extend({
   model: directory.Device,
   showOnlyOnline: true,

   url: function () {
   		url = CbtClient.getUserDevicesUrl();
	   	if (this.showOnlyOnline) {
	   		url = url + "?state=ONLINE";
	   	}	
    	return url;
   }
});
