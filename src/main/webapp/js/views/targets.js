directory.TestTarget = Backbone.Model.extend({
	
});

directory.TestTargetsPageView = Backbone.View.extend({

	initialize: function() {
		this.TestTargetListView = new directory.TestTargetListView();
	},

	render:function () {	 	
	 	this.$el.html(this.template());
	 	// Reset the baseUrl of template manager
        Backbone.TemplateManager.baseUrl = '{name}';
        
        // Create the upload manager object
        var uploadManager = new Backbone.UploadManager({
            uploadUrl: '../rip/v2/testtarget',
            templates: {
                main: 'tpl/upload-manager.main',
                file: 'tpl/upload-manager.file'
            }
        });
        
        // Render it in our div
        uploadManager.renderTo(this.$("div#upload"));

        this.$el.append(this.TestTargetListView.render().el)

        return this;
	 }
});

directory.TestTarget = Backbone.Model.extend({
    
});

directory.TestTargetList = Backbone.Collection.extend({
    model:directory.TestTarget,
    url:function() {
        return CbtClient.getUserTestTargetUrl();
    }
});

directory.TestTargetListItemView = Backbone.View.extend({   
    tagName: "tr",

    initialize: function() {
 
    },

    render:function () {         
        this.$el.html(this.template(this.model.toJSON())); 
        return this;
    }

});

directory.TestTargetListView = Backbone.View.extend({

    initialize: function() {        
        this.TestTargetList = new directory.TestTargetList();
        this.TestTargetList.fetch();
        this.TestTargetList.on("add", this.renderTestTarget, this);
    },

    render:function () {        
        this.$el.html(this.template());     
        return this;
    },

    renderTestTarget: function(item) {
        var itemView = new directory.TestTargetListItemView({
                model:item
            });
        this.$el.find("table.mytesttargets").append(itemView.render().el);        
    }
});
