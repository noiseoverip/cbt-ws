directory.TestRun = Backbone.Model.extend({
   parse: function (response) {
      "use strict";
      response.createdMoment = moment(response.created);
      response.updatedMoment = moment(response.updated);
      response.statusCssClass = getStatusCssClass(response.status);
      return response;
   },

   urlRoot: function () {
      "use strict";
      return CbtClient.getUserTestRunsUrl();
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
      // number of items to return per request/page
      'max': function () {
         "use strict";
         return this.perPage;
      },
      // how many results the request should skip ahead to
      // customize as needed. For the Netflix API, skipping ahead based on
      // page * number of results per page was necessary.
      'offset': function () {
         "use strict";
         return (this.currentPage - 1) * this.perPage;
      }
   },
   parse: function (response) {
      "use strict";
      // totalRecords is returned only for the first page
      if (response.totalRecords !== undefined) {
         this.totalRecords = response.totalRecords;
         this.totalPages = this.totalRecords / this.perPage;
      }
      return response.results;
   }
});