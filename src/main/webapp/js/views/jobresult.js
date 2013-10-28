directory.JobResultView = Backbone.View.extend({
   events: {
      "click .showOutput": "showOutput"
   },

   tagName: "tr",

   initialize: function () {
      "use strict";
      this.listenTo(this.model, 'change', this.render);
   },

   render: function () {
      "use strict";
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

      this.jobResultList = new directory.JobResultList(options);
      this.jobResultList.fetch({
         success: function () {
            self.render();
         }
      });

      this.listenTo(this.jobResultList, 'change', this.render);
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
         model: model
      }));
   }
});