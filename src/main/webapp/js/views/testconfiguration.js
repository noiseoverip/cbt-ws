directory.TestConfigurationView = Backbone.View.extend({
   events: {
      "click #saveTestConfig": "saveTestConfiguration"
   },

   initialize: function () {
      "use strict";
      this.selectedDeviceTypeView = new directory.DeviceTypeListView();
      this.testScriptSelectView = new directory.TestScriptSelectView();
      this.testTargetSelectView = new directory.TestTargetSelectView();
   },

   render: function () {
      "use strict";
      this.$el.html(this.template());
      this.$el.find("#saveTestConfig").before(this.testScriptSelectView.render().el);
      this.$el.find("#saveTestConfig").before(this.testTargetSelectView.render().el);
      this.$el.find("#saveTestConfig").before(this.selectedDeviceTypeView.render().el);
      return this;
   },

   saveTestConfiguration: function (e) {
      "use strict";
      e.preventDefault(); // prevent default button handling

      var form = ConvertFormToJSON(this.$("form"));
      // Create list of selected devices
      var devices = [];
      this.$("form div.deviceTypes input").each(function () {
         if ($(this).prop('checked') == true) {
            devices.push($(this).val());
         }
      });
      var testProfileData = {
         name: "notused",
         mode: form.mode,
         deviceTypes: devices
      };
      var testConfigData = {
         name: form.name,
         testProfileId: "toBeSetLater",
         testScriptId: form.testScriptId,
         testTargetId: form.testTargetId
      };

      CbtClient.createNewTestConfig(testProfileData, testConfigData, function (result) {
         if (result === true) {
            directory.router.navigate("", {trigger: true});
         } else {
            directory.shellView.showAlert("alert-danger", "Please check your selections");
         }

      });

   }

});

directory.TestScriptSelectView = Backbone.View.extend({

   initialize: function () {
      "use strict";
      this.testScriptCollection = new directory.TestScriptList();
      this.testScriptCollection.fetch();
      this.listenTo(this.testScriptCollection, 'sync', this.render);
   },

   render: function () {
      "use strict";
      this.$el.html(this.template({collection: this.testScriptCollection}));
      return this;
   }
});

directory.TestTargetSelectView = Backbone.View.extend({

   initialize: function () {
      "use strict";
      this.testTargetCollection = new directory.TestTargetList();
      this.testTargetCollection.fetch();
      this.listenTo(this.testTargetCollection, 'sync', this.render);
   },

   render: function () {
      "use strict";
      this.$el.html(this.template({collection: this.testTargetCollection}));
      return this;
   }
});


directory.TestConfigurationListView = Backbone.View.extend({

   events: {
      "click #addTestConfig": "addNewTestConfig",
      "click .showTestConfig": "showTestConfig",
      "click .runTest": "runTest"
   },

   initialize: function () {
      "use strict";
      this._views = [];
      this.testConfigList = new directory.TestConfigurationList();
      this.testConfigList.fetch();
      this.listenTo(this.testConfigList, 'sync', this.render);
      this.listenTo(this.testConfigList, 'add', this.pushTestConfig);
   },

   showTestConfig: function (e) {
      "use strict";
      var testConfigId = $(e.target).val();
      console.log("show test config:" + testConfigId);
      alert("not implemented");
   },

   render: function () {
      "use strict";
      this.$el.html(this.template());
      var container = document.createDocumentFragment();
      // render each subview, appending to our root element
      _.each(this._views, function (subview) {
         container.appendChild(subview.render().el);
      });
      this.$el.find("table.testconfigurations").append(container);
      return this;
   },

   pushTestConfig: function (model) {
      "use strict";
      this._views.push(new directory.TestConfigurationListItemView({
         model: model
      }));
   },

   addNewTestConfig: function () {
      "use strict";
      directory.router.navigate("testconfiguration", {trigger: true});
   },

   runTest: function (e) {
      "use strict";
      var testConfigId = $(e.target).val();
      CbtClient.createTestRun(testConfigId, function (message, data) {
         // No data returned means error therefore, show message
         if (data === undefined) {
            alert(message);
         }
         Backbone.trigger('testrun-created', data);
      });
   }
});

directory.TestConfigurationListItemView = Backbone.View.extend({
   tagName: "tr",

   render: function () {
      "use strict";
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   }
});