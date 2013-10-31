directory.DeviceJob = Backbone.Model.extend({});
directory.DeviceJobList = Backbone.Collection.extend({
   model: directory.DeviceJob,

   initialize: function (options) {
      "use strict";
      this.testRunId = options.testRunId;
   },

   url: function () {
      "use strict";
      return CbtClient.getDeviceJobByTestRun(this.testRunId);
   }
});