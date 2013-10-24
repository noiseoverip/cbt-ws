directory.TestTarget = Backbone.Model.extend({
   label: function () {
      return this.get("name");
   }
});

directory.TestTargetList = Backbone.Collection.extend({
   model: directory.TestTarget,
   url: function () {
      return CbtClient.getUserTestTargetUrl();
   }
});
