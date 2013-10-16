directory.JobResult = Backbone.Model.extend({

   initialize: function (options) {
      "use strict";
      this.id = options.id;
      this.title = 'Job result #' + this.id;
   }

});

//directory.JobResultView = Backbone.View.extend({
//   id: 'base-modal',
//   className: 'modal fade hide',
//   events: {
//      'hidden': 'teardown'
//   },
//
//
//   initialize: function (options) {
//      "use strict";
//      _(this).bindAll();
//      this.model = new directory.JobResult(options);
//   },
//
//   show: function () {
//      "use strict";
//      this.$el.modal('show');
//   },
//
//   teardown: function () {
//      "use strict";
//      this.$el.data('modal', null);
//      this.remove();
//   },
//
//   render: function () {
//      "use strict";
//      this.getTemplate(this.template, this.renderView);
//      return this;
//   },
//
//   renderView: function (template) {
//      this.$el.html(template());
//      this.$el.modal({show: false}); // dont show modal on instantiation
//   }
//});

directory.JobResultView = Backbone.View.extend({

   initialize: function (options) {
      "use strict";
      this.model = new directory.JobResult(options);
      this.bind("ok", this.navigateHome);
      this.bind("cancel", this.navigateHome);
   },

   render: function () {
      "use strict";
      this.$el.html(this.template(this.model));
      return this;
   },

   navigateHome: function (modal) {
      "use strict";
      directory.router.navigate("", {trigger: false});
   },

   getTitle: function () {
      "use strict";
      return 'Job result #' + this.model.id;
   }
});