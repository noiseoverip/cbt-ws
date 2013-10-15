directory.JobResult = Backbone.Model.extend({

   initialize: function (options) {
      "use strict";
      this.id = options.id;
      this.title = 'Job result #' + this.id;
   }

});

directory.JobResultView = Backbone.Modal.extend({

   initialize: function (options) {
      "use strict";
      this.model = new directory.JobResult(options);
   },

   render: function () {
      "use strict";
      this.$el.html(this.template(this.model));
      return this;
   }
});