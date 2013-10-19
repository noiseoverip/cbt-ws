directory.TestConfigurationView = Backbone.View.extend({
   events: {
      "click #saveTestConfig": "saveTestConfiguration"
   },

   initialize: function () {
      this.selectedDeviceTypeView = new directory.DeviceTypeListView();
      this.testScriptSelectView = new directory.TestScriptSelectView();
      this.testTargetSelectView = new directory.TestTargetSelectView();
   },

   render: function () {
      this.$el.html(this.template());
      this.$el.find("#saveTestConfig").before(this.testScriptSelectView.render().el);
      this.$el.find("#saveTestConfig").before(this.testTargetSelectView.render().el);
      this.$el.find("#saveTestConfig").before(this.selectedDeviceTypeView.render().el);
      return this;
   },

   saveTestConfiguration: function (e) {
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
         if (result == true) {
            directory.router.navigate("", {trigger: true});
         } else {
            directory.shellView.showAlert("alert-danger", "Please check your selections");
         }

      });

   }

});

directory.TestScriptSelectView = Backbone.View.extend({

   initialize: function () {
      this.testScriptCollection = new directory.UserTestScripts();
      this.testScriptCollection.fetch();
      this.testScriptCollection.on("sync", this.render, this);
   },

   render: function () {
      this.$el.html(this.template({collection: this.testScriptCollection}));
      return this;
   }
});

directory.TestTargetSelectView = Backbone.View.extend({

   initialize: function () {
      this.testTargetCollection = new directory.UserTestTargets();
      this.testTargetCollection.fetch();
      this.testTargetCollection.on("sync", this.render, this);
   },

   render: function () {
      this.$el.html(this.template({collection: this.testTargetCollection}));
      return this;
   }
});

directory.DeviceTypeListView = Backbone.View.extend({

   initialize: function () {
      this.deviceTypeList = new directory.DeviceTypeList();
      this.deviceTypeList.on("add", this.renderItems, this);
      this.deviceTypeList.fetch();
   },

   render: function () {
      this.$el.html(this.template());
      return this;
   },

   renderItems: function (item) {
      var deviceTypeListItemView = new directory.DeviceTypeListItemView({
         model: item
      });
      this.$el.find(".devicetypes div.panel-body").append(deviceTypeListItemView.render().el);
   },

});

directory.DeviceTypeListItemView = Backbone.View.extend({
   tag: "div",
   className: "checkbox",
   render: function () {
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   },
});

directory.TestConfigurationListView = Backbone.View.extend({

   events: {
      "click #addTestConfig": "addNewTestConfig",
      "click .showTestConfig": "showTestConfig",
      "click .runTest": "runTest"
   },

   initialize: function () {
      this.testConfigList = new directory.TestConfigurationList();
      this.testConfigList.fetch();
      this.testConfigList.on("add", this.renderTestConfig, this);
   },

   showTestConfig: function (e) {
      var testConfigId = $(e.target).val();
      console.log("show test config:" + testConfigId);
      alert("not implemented");
   },

   render: function () {
      this.$el.html(this.template());
      return this;
   },

   renderTestConfig: function (item) {
      var testConfigListItemView = new directory.TestConfigurationListItemView({
         model: item
      });
      this.$el.find("table.testconfigurations").append(testConfigListItemView.render().el);
   },

   addNewTestConfig: function () {
      directory.router.navigate("testconfiguration", {trigger: true});
   },

   runTest: function (e) {
      var testConfigId = $(e.target).val();
      CbtClient.createTestRun(testConfigId, function (message, data) {         
         Backbone.trigger('testrun-created', data);
      });
   }
});

directory.TestConfigurationListItemView = Backbone.View.extend({
   tagName: "tr",

   initialize: function () {

   },

   render: function () {
      this.$el.html(this.template(this.model.toJSON()));
      return this;
   }
});


directory.TestConfiguration = Backbone.Model.extend({

});

directory.TestScript = Backbone.Model.extend({
   label: function () {
      return this.get("name");
   }
});

directory.TestTarget = Backbone.Model.extend({
   label: function () {
      return this.get("name");
   }
});

directory.UserTestScripts = Backbone.Collection.extend({
   model: directory.TestScript,

   url: function () {
      return CbtClient.getUserTestScriptsUrl();
   }
});

directory.TestConfigurationList = Backbone.Collection.extend({
   model: directory.TestConfiguration,
   url: function () {
      return CbtClient.getUserTestConfigsUrl();
   }
});

directory.UserTestTargets = Backbone.Collection.extend({
   model: directory.TestTarget,

   url: function () {
      return CbtClient.getUserTestTargetsUrl();
   }
});

directory.DeviceType = Backbone.Model.extend({

});

directory.DeviceTypeList = Backbone.Collection.extend({
   model: directory.DeviceType,

   url: function () {
      return CbtClient.getDeviceTypesUrl();
   }
});
