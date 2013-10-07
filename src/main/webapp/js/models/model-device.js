directory.Device = Backbone.Model.extend();

directory.DeviceCollection = Backbone.Collection.extend({
	model: directory.Device,
	url: "rip/user/1/device"	
})