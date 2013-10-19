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

   initialize: function() {
         this.model.bind('change', this.render, this);
         this.model.bind('destroy', this.remove, this);
   },

   render: function () {
      "use strict";
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   }
});

directory.TestRunList = Backbone.Paginator.requestPager.extend({
   model: directory.TestRun,
   // url: function () {
   //    "use strict";
   //    return CbtClient.getUserTestRunsUrl();
   // },
   paginator_core: {
      // the type of the request (GET by default)
      type: 'GET',

      // the type of reply (jsonp by default)
      dataType: 'json',

      // the URL (or base URL) for the service
      // if you want to have a more dynamic URL, you can make this a function
      // that returns a string
      url: CbtClient.getUserTestRunsUrl()
   },
   paginator_ui: {
      // the lowest page index your API allows to be accessed
      firstPage: 1,

      // which page should the paginator start from
      // (also, the actual page the paginator is on)
      currentPage: 1,

      // how many items per page should be shown
      perPage: 10,

      // a default number of total pages to query in case the API or
      // service you are using does not support providing the total
      // number of pages for us.
      // 10 as a default in case your service doesn't return the total
      totalPages: 10
   },
   server_api: {
      // the query field in the request
      '$filter': '',

      // number of items to return per request/page
      'max': function() { return this.perPage },

      // how many results the request should skip ahead to
      // customize as needed. For the Netflix API, skipping ahead based on
      // page * number of results per page was necessary.
      'offset': function() { return (this.currentPage-1) * this.perPage },

      // field to sort by
      '$orderby': 'ReleaseYear',

      // what format would you like to request results in?
      '$format': 'json',      
   },
   parse: function(response) {
      if (response.totalRecords != undefined) {
         this.totalRecords = response.totalRecords;
         this.totalPages = this.totalRecords / this.perPage;      
      }
      return response.results;
   }
});


directory.TestRunListView = Backbone.View.extend({

   events: {
      "click .removeTestRun": "deleteTestRun",
      "click .showTestRun": "showTestRun",      
   },

   initialize: function () {
      "use strict";
      this.collection.pager();
      //this.collection.on("add", this.renderItem, this);
      this.collection.on("reset", this.refresh, this);
      this.collection.on("sync", this.sync, this);
      this.paginatedView = new directory.PaginatedView({
         collection: this.collection,
      });

   },
   sync: function() {
      console.log("sync " + this.collection.length);
      this.$("table.testruns").empty();     
      this.collection.each(this.renderItem, this);
   },

   refresh: function () {
      "use strict";
      console.log("reset" + this.collection.length);      
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
      var table = this.$("table.testruns");
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