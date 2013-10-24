directory.JobResultView = Backbone.View.extend({

   render: function () {
      "use strict";
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   }

});

directory.JobResultListView = Backbone.View.extend({
   template: '',

   initialize: function (options) {
      "use strict";
      this.jobResultList = new directory.JobResultList(options);
      this.jobResultList.fetch();
      this.jobResultList.on("add", this.renderItem, this);
      this.jobResultList.on("reset", this.refresh, this);
   },

   render: function () {
      "use strict";
      this.$el.html(this.template);
      return this;
   },

   renderItem: function (item) {
      "use strict";
      var jobResultView = new directory.JobResultView({
         model: item
      });
      this.$el.append(jobResultView.render().el);
   },

   refresh: function () {
      "use strict";
      this.jobResultList.fetch();
      this.render();
   }

});