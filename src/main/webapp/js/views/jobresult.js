directory.JobResultView = Backbone.View.extend({
   events: {
      "click .showOutput": "showOutput"
   },

   tagName: "tr",

   initialize: function (options) {
      "use strict";
      this.serialNumber = '';
      this.device = new directory.Device({id: options.deviceId});
      this.device.fetch();
      this.listenTo(this.device, 'sync', this.renderDevice);
      this.listenTo(this.model, 'sync', this.render);
   },

   renderDevice: function () {
      "use strict";
      this.serialNumber = this.device.attributes.serialNumber;
      this.render();
   },

   render: function () {
      "use strict";
      this.model.set('serialNumber', this.serialNumber);
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   },

   showOutput: function () {
      "use strict";
      var w = window.open();
      if (null === this.model.attributes.output || '' === this.model.attributes.output) {
         this.model.attributes.output = 'NO OUTPUT RECORDED';
      }
      $(w.document.body).html('<pre>' + this.model.attributes.output + '</pre>');
   }

});

directory.JobResultListView = Backbone.View.extend({

   tagName: 'tbody',

   initialize: function (options) {
      "use strict";
      var self = this;
      this._views = [];

      this.deviceId = options.deviceId;
      this.jobResultList = new directory.JobResultList({deviceJobId: options.deviceJobId});
      this.jobResultList.fetch();

      this.listenTo(this.jobResultList, 'sync', this.render);
      this.listenTo(this.jobResultList, 'add', this.pushItem);
   },

   render: function () {
      "use strict";
      this.$el.empty();
      var container = document.createDocumentFragment();
      // render each subview, appending to our root element
      _.each(this._views, function (subview) {
         container.appendChild(subview.render().el);
      });
      this.$el.append(container);
      return this;
   },

   pushItem: function (model) {
      "use strict";
      this._views.push(new directory.JobResultView({
         model: model,
         deviceId: this.deviceId
      }));
   }
});