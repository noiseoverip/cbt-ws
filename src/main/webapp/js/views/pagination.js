directory.PaginatedView = Backbone.View.extend({

    events: {
      'click a.servernext': 'nextResultPage',
      'click a.serverprevious': 'previousResultPage',
      'click a.orderUpdate': 'updateSortBy',
      'click a.serverlast': 'gotoLast',
      'click a.page': 'gotoPage',
      'click a.serverfirst': 'gotoFirst',
      'click a.serverpage': 'gotoPage',
      'click .serverhowmany a': 'changeCount'
    },     

    initialize: function () {
      this.collection.on('reset', this.render, this);
      this.collection.on('sync', this.render, this);    
    },

    render: function () {      
      this.$el.html(this.template(this.collection.info()));
      return this;  
    },

    updateSortBy: function (e) {
      e.preventDefault();
      var currentSort = $('#sortByField').val();
      this.collection.updateOrder(currentSort);
    },

    nextResultPage: function (e) {
      e.preventDefault();
      this.collection.requestNextPage();
    },

    previousResultPage: function (e) {
      e.preventDefault();
      this.collection.requestPreviousPage();
    },

    gotoFirst: function (e) {
      e.preventDefault();
      this.collection.goTo(this.collection.information.firstPage);
    },

    gotoLast: function (e) {
      e.preventDefault();
      this.collection.goTo(this.collection.information.lastPage);
    },

    gotoPage: function (e) {
      e.preventDefault();
      var page = $(e.target).text();
      this.collection.goTo(page);
    },

    changeCount: function (e) {
      e.preventDefault();
      var per = $(e.target).text();
      this.collection.howManyPer(per);
    }
});
