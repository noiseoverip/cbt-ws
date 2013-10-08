directory.TestScript = Backbone.Model.extend({
	
});

directory.TestScriptsPageView = Backbone.View.extend({

	initialize: function() {
		
	},

	render:function () {	 	
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

        return this;
	 }
});