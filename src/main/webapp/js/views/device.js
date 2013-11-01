directory.DeviceListView = Backbone.View.extend({
   events: {
      "click .showDevice": "showDevice"
   },

   initialize: function () {
      "use strict";
      this._views = [];
      this.deviceList = new directory.DeviceList();
      this.deviceList.showOnlyOnline = true; //todo: change to be triggered by events
      this.deviceList.fetch();
      this.listenTo(this.deviceList, 'add', this.pushDevice);
      this.listenTo(this.deviceList, 'sync', this.render);
   },

   render: function () {
      "use strict";
      this.$el.html(this.template());
      var container = document.createDocumentFragment();
      // render each subview, appending to our root element
      _.each(this._views, function (subview) {
         container.appendChild(subview.render().el);
      });
      this.$el.find("table.mydevices").append(container);
      return this;
   },

   showDevice: function (e) {
      "use strict";
      var deviceId = $(e.target).val();
      directory.router.navigate("device/" + deviceId, {trigger: true});
   },

   pushDevice: function (model) {
      "use strict";
      this._views.push(new directory.DeviceListItemView({
         model: model
      }));
   }
});

directory.DeviceListItemView = Backbone.View.extend({
   tagName: "tr",

   render: function () {
      "use strict";
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   }

});

directory.DeviceView = Backbone.View.extend({
   events: {
      "click .deleteShare": "deleteShare"
   },

   initialize: function () {
      "use strict";
      this.model.fetch();
      this.listenTo(this.model, 'sync', this.checkIfOwner);
   },

   checkIfOwner: function () {
      "use strict";
      if (this.model.get("listerIsOwner") === true) {
         this.render();
         this.deviceShareList = new directory.DeviceShareList([], { deviceId: this.model.get("id")});
         this.deviceShareView = new directory.DeviceShareListView({
            el: this.$el.find("div.sharing"),
            model: this.model,
            collection: this.deviceShareList
         });
         this.deviceShareList.fetch();
      } else {
         this.render();
      }
   },

   render: function () {
      "use strict";
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   }

});