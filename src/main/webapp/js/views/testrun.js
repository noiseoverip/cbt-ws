directory.TestRunListView = Backbone.View.extend({

   events: {
      "click .removeTestRun": "deleteTestRun",
      "click .showTestRun": "showTestRun"
   },

   listTable: "table.testruns tbody",

   initialize: function () {
      "use strict";
      this.collection.pager();
      this.listenTo(this.collection, 'add', this.renderItem);
      this.listenTo(this.collection, 'sync', this.sync);
      this.paginatedView = new directory.PaginatedView({
         collection: this.collection
      });

   },
   sync: function () {
      "use strict";
      this.$(this.listTable).empty();
      this.collection.each(this.renderItem, this);
   },

   render: function () {
      "use strict";
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
      if (options.at === 0) {
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
      var testRunId = $(e.target).data('value');
      directory.router.navigate("jobresult/" + testRunId, {trigger: true});
   }
});

directory.TestRunListItemView = Backbone.View.extend({
   tagName: "tr",

   initialize: function () {
      "use strict";
      this.listenTo(this.model, 'sync', this.render);
      this.listenTo(this.model, 'destroy', this.remove);
   },

   render: function () {
      "use strict";
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   }
});

directory.TestRunResultView = Backbone.View.extend({

   initialize: function (options) {
      "use strict";
      this._views = [];

      this.testRun = new directory.TestRun({id: options.testRunId, statusCssClass: '', created: '', config: ''});
      this.testRun.fetch();

      this.testConfig = new directory.TestConfiguration({name: ''});

      this.deviceJobList = new directory.DeviceJobList(options);
      this.deviceJobList.fetch();

      this.listenTo(this.testRun, 'sync', this.syncTestRun);
      this.listenTo(this.deviceJobList, 'sync', this.render);
      this.listenTo(this.deviceJobList, 'add', this.pushItem);

      this.bind("ok", this.navigateHome);
      this.bind("cancel", this.navigateHome);
   },

   render: function () {
      "use strict";
      this.testRun.set('deviceCount', this.deviceJobList.length);
      this.testRun.set('config', this.testConfig.attributes.name);
      this.$el.html(this.template(this.testRun.toJSON()));
      var container = document.createDocumentFragment();
      // render each subview, appending to our root element
      _.each(this._views, function (subview) {
         container.appendChild(subview.render().el);
      });
      this.$el.find("table.devicejobresults").append(container);
      return this;
   },

   pushItem: function (model) {
      "use strict";
      // create a sub view for every model in the collection
      this._views.push(new directory.JobResultListView({
         deviceJobId: model.id,
         deviceId: model.attributes.deviceId
      }));
   },

   navigateHome: function () {
      "use strict";
      directory.router.navigate("", {trigger: false});
   },

   getTitle: function () {
      "use strict";
      return 'Result #' + this.deviceJobList.testRunId;
   },

   syncTestRun: function () {
      "use strict";
      this.testConfig.set('id', this.testRun.attributes.testconfigId);
      this.testConfig.fetch();
      this.listenTo(this.testConfig, 'sync', this.render);
   }
});