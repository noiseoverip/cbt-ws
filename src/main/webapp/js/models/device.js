directory.Device = Backbone.Model.extend({
   parse: function (response) {
      "use strict";
      response.updatedMoment = moment(response.updated);
      return response;
   },

   urlRoot: function () {
      "use strict";
      return CbtClient.getUserDevicesUrl();
   }
});

directory.DeviceList = Backbone.Collection.extend({
   model: directory.Device,
   showOnlyOnline: true,

   url: function () {
      "use strict";
      var url = CbtClient.getUserDevicesUrl();
      if (this.showOnlyOnline) {
         url = url + "?state=ONLINE";
      }
      return url;
   }
});
