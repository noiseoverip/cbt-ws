directory.TestConfiguration = Backbone.Model.extend({
});

directory.TestConfigurationList = Backbone.Collection.extend({
   model: directory.TestConfiguration,
   url: function () {
      return CbtClient.getUserTestConfigsUrl();
   }
});