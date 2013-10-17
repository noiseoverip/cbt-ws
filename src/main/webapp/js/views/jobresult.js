directory.JobResult = Backbone.Model.extend({});
directory.DeviceJob = Backbone.Model.extend({});

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

directory.DeviceJobList = Backbone.Collection.extend({
   model: directory.DeviceJob,

   initialize: function (options) {
      "use strict";
      this.testRunId = options.testRunId;
   },

   url: function () {
      "use strict";
      return CbtClient.getDeviceJob(this.testRunId);
   }
});

directory.JobResultView = Backbone.View.extend({

   render: function () {
      "use strict";
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   }

});

directory.JobResultListView = Backbone.View.extend({
   template: '',

   initialize: function (options) {
      "use strict";
      this.jobResultList = new directory.JobResultList(options);
      this.jobResultList.fetch();
      this.jobResultList.on("add", this.renderItem, this);
      this.jobResultList.on("reset", this.refresh, this);
   },

   render: function () {
      "use strict";
      this.$el.html(this.template);
      return this;
   },

   renderItem: function (item) {
      "use strict";
      var jobResultView = new directory.JobResultView({
         model: item
      });
      this.$el.append(jobResultView.render().el);
   },

   refresh: function () {
      "use strict";
      this.jobResultList.fetch();
      this.render();
   }

});


directory.TestRunResultView = Backbone.View.extend({

   template: '',

   initialize: function (options) {
      "use strict";
      this.deviceJobList = new directory.DeviceJobList(options);
      this.deviceJobList.fetch();
      this.deviceJobList.on("add", this.renderItem, this);
      this.deviceJobList.on("reset", this.refresh, this);
      this.bind("ok", this.navigateHome);
      this.bind("cancel", this.navigateHome);
   },

   render: function () {
      "use strict";
      this.$el.html(this.template);
      return this;
   },

   renderItem: function (item) {
      "use strict";
      var jobResultListView = new directory.JobResultListView({deviceJobId: item.id});
      this.$el.append(jobResultListView.render().el);
   },

   refresh: function () {
      "use strict";
      this.deviceJobList.fetch();
      this.render();
   },

   navigateHome: function (modal) {
      "use strict";
      directory.router.navigate("", {trigger: false});
   },

   getTitle: function () {
      "use strict";
      return 'Job result #' + this.deviceJobList.testRunId;
   }
});