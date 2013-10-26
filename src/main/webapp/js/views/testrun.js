directory.TestRunListView = Backbone.View.extend({

   events: {
      "click .removeTestRun": "deleteTestRun",
      "click .showTestRun": "showTestRun"
   },

   listTable: "table.testruns tbody",

   initialize: function () {
      "use strict";
      this.collection.pager();
      this.collection.on("add", this.renderItem, this);
      this.collection.on("sync", this.sync, this);
      this.paginatedView = new directory.PaginatedView({
         collection: this.collection,
      });

   },
   sync: function () {
      this.$(this.listTable).empty();
      this.collection.each(this.renderItem, this);
   },

   render: function () {
      this.$el.html(this.template());
      this.$("#pagination").append(this.paginatedView.el);
      return this;
   },

   renderItem: function (item, collection, options) {
      "use strict";
      var itemView = new directory.TestRunListItemView({
         model: item
      });
      var renderedItem = itemView.render().el;
      if (options.at == 0) {
         this.$(this.listTable).prepend(renderedItem);
      } else {
         this.$(this.listTable).append(renderedItem);
      }
   },

   deleteTestRun: function (e) {
      "use strict";
      var testRunId = $(e.target).val();
      alert("not implemented to delete test run id:" + testRunId);
   },

   showTestRun: function (e) {
      "use strict";
      var testRunId = $(e.target).data('value')
      directory.router.navigate("jobresult/" + testRunId, {trigger: true});
   }
});

directory.TestRunListItemView = Backbone.View.extend({
   tagName: "tr",

   initialize: function () {
      this.model.bind('change', this.render, this);
      this.model.bind('destroy', this.remove, this);
   },

   render: function () {
      "use strict";
      this.$el.html(this.template(this.model.toJSON()));
      return this;
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
      return 'Result #' + this.deviceJobList.testRunId;
   }
});