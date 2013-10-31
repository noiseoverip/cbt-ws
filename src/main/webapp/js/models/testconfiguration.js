directory.TestConfiguration = Backbone.Model.extend({
   urlRoot: function () {
      "use strict";
      return CbtClient.getUserTestConfigUrl();
   }
});

directory.TestConfigurationList = Backbone.Collection.extend({
   model: directory.TestConfiguration,

   url: function () {
      "use strict";
      return CbtClient.getUserTestConfigUrl();
   }
});