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
      "click .showTestRun": "showTestRun",      
   },

   initialize: function () {
      "use strict";
      this.collection.fetch();
      this.collection.on("add", this.renderItem, this);
      this.collection.on("reset", this.refresh, this);
   },

   refresh: function () {
      "use strict";
      this.collection.fetch();
      this.render();
   },

   render: function () {
      "use strict";
      var that = this;
      this.$el.html(this.template());
      return this;
   },

   renderItem: function (item, collection, options) {
      "use strict";      
      var itemView = new directory.TestRunListItemView({
         model: item
      });
      var table = this.$el.find("table.testruns");
      var renderedItem = itemView.render().el;
      if (options.at == 0) {      
         table.prepend(renderedItem);
      } else {
         table.append(renderedItem);
      }     
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