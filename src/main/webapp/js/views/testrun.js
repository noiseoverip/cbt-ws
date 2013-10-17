directory.TestRun = Backbone.Model.extend({
   parse: function (response) {
      "use strict";
      response.createdMoment = moment(response.created);
      response.updatedMoment = moment(response.updated);
      return response;
   }
});

directory.TestRunListItemView = Backbone.View.extend({
   tagName: "tr",

   render: function () {
      "use strict";
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   }
});

directory.TestRunList = Backbone.Collection.extend({
   model: directory.TestRun,
   url: function () {
      "use strict";
      return CbtClient.getUserTestRunsUrl();
   }
});


directory.TestRunListView = Backbone.View.extend({

   events: {
      "click .removeTestRun": "deleteTestRun",
      "click .showTestRun": "showTestRun"
   },

   initialize: function () {
      "use strict";
      this.testRunListList = new directory.TestRunList();
      this.testRunListList.fetch();
      this.testRunListList.on("add", this.renderItem, this);
      this.testRunListList.on("reset", this.refresh, this);
   },

   refresh: function () {
      "use strict";
      this.testRunListList.fetch();
      this.render();
   },

   render: function () {
      "use strict";
      var that = this;
      this.$el.html(this.template());
      return this;
   },

   renderItem: function (item) {
      "use strict";
      var itemView = new directory.TestRunListItemView({
         model: item
      });
      this.$el.find("table.testruns").append(itemView.render().el);
   },

   deleteTestRun: function (e) {
      "use strict";
      var testRunId = $(e.target).val();
      alert("not implemented to delete test run id:" + testRunId);
   },

   showTestRun: function (e) {
      "use strict";
      var testRunId = $(e.target).val();
      directory.router.navigate("jobresult/" + testRunId, {trigger: true});
   }
});