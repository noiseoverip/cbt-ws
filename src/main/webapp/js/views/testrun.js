directory.TestRun = Backbone.Model.extend({
	parse: function(response) {
		response.createdMoment = moment(response.created);
		response.updatedMoment = moment(response.updated);
		return response;
	}
});

directory.TestRunListItemView = Backbone.View.extend({	
	tagName: "tr",

	initialize: function() {
 
	},

	render:function () {	 	 
      	this.$el.html(this.template(this.model.toJSON())); 
        return this;
    }
});

directory.TestRunList = Backbone.Collection.extend({
	model:directory.TestRun,
	url:function() {
		return CbtClient.getUserTestRunsUrl();
	}
});


directory.TestRunListView = Backbone.View.extend({

	initialize: function() {
		this.testRunListList = new directory.TestRunList();
		this.testRunListList.fetch();
		this.testRunListList.on("add", this.renderItem, this);
	},

	render:function () {
	 	var that = this;
	 	this.$el.html(this.template());	 	
	 	return this;
    },

    renderItem: function(item) {
    	var itemView = new directory.TestRunListItemView({
				model:item
			});
		this.$el.find("table.testruns").append(itemView.render().el);		
    }
});