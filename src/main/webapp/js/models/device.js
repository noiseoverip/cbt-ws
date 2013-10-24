directory.Device = Backbone.Model.extend({
   parse: function (response) {
      response.updatedMoment = moment(response.updated);
      return response;
   }
});

directory.DeviceList = Backbone.Collection.extend({
   model: directory.Device,
   url: function () {
      return CbtClient.getUserDevicesUrl();
   }
});
