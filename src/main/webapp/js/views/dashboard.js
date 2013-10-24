directory.DashBoardView = Backbone.View.extend({

   initialize: function () {
      this.testRunListCollection = new directory.TestRunList();
      this.testRunListView = new directory.TestRunListView({
         collection : this.testRunListCollection
      });      

      this.testConfigurationListView = new directory.TestConfigurationListView();
      this.deviceListView = new directory.DeviceListView();

      this.listenTo( Backbone, 'testrun-created', function (newTestRun) {      
         // Tell pager to re-fetch first first page
         this.testRunListCollection.reset();
         this.testRunListCollection.updateOrder();
         this.testRunListCollection.goTo(1);
      }, this); 
   },

   render: function () {
      this.$el.html(this.template());
      this.$el.append(this.testRunListView.render().el);
      this.$el.append(this.testConfigurationListView.render().el);
      this.$el.append(this.deviceListView.render().el);
   }

});