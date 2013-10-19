directory.DevicePageView = Backbone.View.extend({

   initialize: function () {
      this.testRunListCollection = new directory.TestRunList();
      this.testRunListView = new directory.TestRunListView({
         collection : this.testRunListCollection
      });      

      this.testConfigurationListView = new directory.TestConfigurationListView();
      this.deviceListView = new directory.DeviceListView();

      this.listenTo( Backbone, 'testrun-created', function (newTestRun) {      
         // add new test run at index 0
         this.testRunListCollection.add(newTestRun, {at: 0})
      }, this); 
   },

   render: function () {
      this.$el.html(this.template());
      this.$el.append(this.testRunListView.render().el);
      this.$el.append(this.testConfigurationListView.render().el);
      this.$el.append(this.deviceListView.render().el);
   }

});

directory.DeviceListView = Backbone.View.extend({

   initialize: function () {
      this.deviceList = new directory.DeviceList();
      this.deviceList.fetch();
      this.deviceList.on("add", this.renderDevice, this);
   },

   render: function () {
      this.$el.html(this.template());
      return this;
   },

   renderDevice: function (item) {
      var deviceListItemView = new directory.DeviceListItemView({
         model: item
      });
      this.$el.find("table.mydevices").append(deviceListItemView.render().el);
   }

});

// Device Model
directory.Device = Backbone.Model.extend({
   parse: function (response) {
      response.updatedMoment = moment(response.updated);
      return response;
   }
});

// Device view
directory.DeviceListItemView = Backbone.View.extend({
   tagName: "tr",

   initialize: function () {

   },

   render: function () {
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   }

});

// Device collection
directory.DeviceList = Backbone.Collection.extend({
   model: directory.Device,
   url: function () {
      return CbtClient.getUserDevicesUrl();
   }
});
