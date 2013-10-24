directory.TestScriptsPageView = Backbone.View.extend({

   initialize: function () {
      this.testScriptListView = new directory.TestScriptListView();
   },

   render: function () {
      this.$el.html(this.template());
      // Reset the baseUrl of template manager
      Backbone.TemplateManager.baseUrl = '{name}';

      // Create the upload manager object
      var uploadManager = new Backbone.UploadManager({
         uploadUrl: '../rip/v2/testscript',
         templates: {
            main: 'tpl/upload-manager.main',
            file: 'tpl/upload-manager.file'
         }
      });

      // Render it in our div
      uploadManager.renderTo(this.$("div#upload"));

      this.$el.append(this.testScriptListView.render().el)

      return this;
   }
});

directory.TestScriptListView = Backbone.View.extend({

   initialize: function () {
      this.testScriptList = new directory.TestScriptList();
      this.testScriptList.fetch();
      this.testScriptList.on("add", this.renderTestScript, this);
   },

   render: function () {
      this.$el.html(this.template());
      return this;
   },

   renderTestScript: function (item) {
      var itemView = new directory.TestScriptListItemView({
         model: item
      });
      this.$el.find("table.mytestscripts").append(itemView.render().el);
   }
});

directory.TestScriptListItemView = Backbone.View.extend({
   tagName: "tr",

   initialize: function () {

   },

   render: function () {
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   }

});