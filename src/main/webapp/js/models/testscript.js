directory.TestScript = Backbone.Model.extend({
   label: function () {
      "use strict";
      return this.get("name");
   },

   urlRoot: function () {
      "use strict";
      return CbtClient.getUserTestScriptUrl();
   }
});

directory.TestScriptList = Backbone.Collection.extend({
   model: directory.TestScript,

   url: function () {
      "use strict";
      return CbtClient.getUserTestScriptUrl();
   }
});