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

	events: {
		"click .removeTestRun":"deleteTestRun"
	},

	initialize: function() {
		this.testRunListList = new directory.TestRunList();
		this.testRunListList.fetch();
		this.testRunListList.on("add", this.renderItem, this);
		this.testRunListList.on("reset", this.refresh, this);
	},

	refresh: function() {
		this.testRunListList.fetch();
		this.render();
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
    }, 

    deleteTestRun: function(e) {
    	var testRunId = $(e.target).val();
    	alert("not implemented to delete test run id:" + testRunId);
    }   
});