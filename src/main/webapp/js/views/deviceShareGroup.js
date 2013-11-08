directory.DeviceShareGroupListItemView = Backbone.View.extend({
   tagName: "tr",

   events: {
      "click .delete":"deleteSharing"
   },

   deleteSharing:function () {
      this.model.destroy();     
      this.remove();
   },

   initialize: function () {
   },

   render: function () {
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   }

});

directory.DeviceShareGroupListView = Backbone.View.extend({  
   events: {
      "click .shareDevice": "newShare" 
   },

   initialize: function () {      
      this.collection.on("add", this.renderItem, this);
      this.collection.on("reset", this.emptyTable, this);       
      this.render();
   },

   emptyTable: function() {
      this.$el.find("table").empty();
   },

   render: function () {
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   }, 

   renderItem: function (item) {      
      var modelView = new directory.DeviceShareGroupListItemView({
         model: item
      });
      this.$el.find("table").append(modelView.render().el);
   },

   newShare: function() {
      var that = this;
      var groupName = this.$el.find("input.username").val();
      if (groupName != "all") {
         alert("You can only use group <All>");
         return;
      }

      CbtClient.createDeviceShareGroup(this.model.id, 1, function(success, data) {
         if (success) {
            that.collection.reset();
            that.collection.fetch();
         } else {
            alert(data.responseText);
         }
      });
   }   
});