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

directory.DeviceListItemView = Backbone.View.extend({
   tagName: "tr",

   initialize: function () {

   },

   render: function () {
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   }

});