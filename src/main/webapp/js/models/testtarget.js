directory.TestTarget = Backbone.Model.extend({
   label: function () {
      "use strict";
      return this.get("name");
   },

   urlRoot: function () {
      "use strict";
      return CbtClient.getUserTestTargetUrl();
   }
});

directory.TestTargetList = Backbone.Collection.extend({
   model: directory.TestTarget,

   url: function () {
      "use strict";
      return CbtClient.getUserTestTargetUrl();
   }
});
