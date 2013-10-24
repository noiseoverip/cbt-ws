directory.TestScript = Backbone.Model.extend({
   label: function () {
      return this.get("name");
   }
});

directory.TestScriptList = Backbone.Collection.extend({
   model: directory.TestScript,
   url: function () {
      return CbtClient.getUserTestScriptUrl();
   }
});