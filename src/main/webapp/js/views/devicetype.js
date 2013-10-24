directory.DeviceTypeListView = Backbone.View.extend({

   initialize: function () {
      this.deviceTypeList = new directory.DeviceTypeList();
      this.deviceTypeList.on("add", this.renderItems, this);
      this.deviceTypeList.fetch();
   },

   render: function () {
      this.$el.html(this.template());
      return this;
   },

   renderItems: function (item) {
      var deviceTypeListItemView = new directory.DeviceTypeListItemView({
         model: item
      });
      this.$el.find(".devicetypes div.panel-body").append(deviceTypeListItemView.render().el);
   },

});

directory.DeviceTypeListItemView = Backbone.View.extend({
   tag: "div",
   className: "checkbox",
   render: function () {
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   },
});