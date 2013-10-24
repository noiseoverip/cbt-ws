directory.DeviceType = Backbone.Model.extend({
});

directory.DeviceTypeList = Backbone.Collection.extend({
   model: directory.DeviceType,

   url: function () {
      return CbtClient.getDeviceTypesUrl();
   }
});