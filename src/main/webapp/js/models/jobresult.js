directory.JobResult = Backbone.Model.extend({
   parse: function(response) {
      "use strict";
      response.labelClass = "label-" + getStatusCssClass(response.state);
      return response;
   }
});
directory.JobResultList = Backbone.Collection.extend({
   model: directory.JobResult,

   initialize: function (options) {
      "use strict";
      this.deviceJobId = options.deviceJobId;
   },

   url: function () {
      "use strict";
      return CbtClient.getDeviceJobResult(this.deviceJobId);
   }
});