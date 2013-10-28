directory.TestConfiguration = Backbone.Model.extend({
   url: function () {
      "use strict";
      return CbtClient.getUserTestConfigUrl(this.id);
   }
});

directory.TestConfigurationList = Backbone.Collection.extend({
   model: directory.TestConfiguration,
   url: function () {
      "use strict";
      return CbtClient.getUserTestConfigsUrl();
   }
});