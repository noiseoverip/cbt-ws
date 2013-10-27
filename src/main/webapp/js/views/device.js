directory.DeviceListView = Backbone.View.extend({
   events: {
      "click .showDevice":"showDevice"
   },

   initialize: function () {
      this.deviceList = new directory.DeviceList();
      this.deviceList.showOnlyOnline=true; //todo: change to be triggered by events
      this.deviceList.fetch();
      this.deviceList.on("add", this.renderDevice, this);
   },

   render: function () {
      this.$el.html(this.template());
      return this;
   },

   showDevice: function(e) {
      var deviceId = $(e.target).val();     
      directory.router.navigate("device/" + deviceId, {trigger: true});
   },

   renderDevice: function (item) {
      var deviceListItemView = new directory.DeviceListItemView({
         model: item
      });
      this.$el.find("table.mydevices").append(deviceListItemView.render().el);
   }

});

directory.DeviceListItemView = Backbone.View.extend({
   tagName: "tr",

   initialize: function () {

   },

   render: function () {
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   }

});

directory.DeviceView = Backbone.View.extend({

   initialize: function () {
      this.model.fetch();
      this.model.on("sync", this.render, this);
   },

   render: function () {
      console.log("render");
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   }

});