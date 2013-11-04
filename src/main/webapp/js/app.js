var directory = {

   views: {},

   models: {},

   loadTemplates: function (views, callback) {
      "use strict";
      var deferreds = [];

      $.each(views, function (index, view) {
         if (directory[view]) {
            deferreds.push($.get('tpl/' + view + '.html', function (data) {
               directory[view].prototype.template = _.template(data);
            }, 'html'));
         } else {
            alert(view + " not found");
         }
      });

      $.when.apply(null, deferreds).done(callback);
   }
};

directory.Router = Backbone.Router.extend({

   routes: {
      "": "dashboard",
      "login": "login",
      "register": "register",
      "testconfiguration": "testconfiguration",
      "testscripts": "tests",
      "applications": "targets",
      "jobresult/:id": "jobresult",
      "device/:id":"device"
   },

   initialize: function () {
      "use strict";
      this.on("authCredentials", this.authCredentials, this);
      this.on("loggedOff", this.onLoggedOff, this);

      directory.shellView = new directory.ShellView();
      $('body').html(directory.shellView.render().el);
      this.$content = $("#content");
   },

   login: function () {
      "use strict";
      directory.loginView = new directory.LoginPageView();
      directory.loginView.render();
      this.$content.html(directory.loginView.el);
      directory.shellView.trigger('loginPageShow', 'ddd');
   },

   register: function() {
      "use strict";
      directory.registrationPageView = new directory.RegistrationPageView();      
      this.$content.html(directory.registrationPageView.render().el);
   },

   dashboard: function () {
      "use strict";
      directory.dashBoardView = new directory.DashBoardView();
      directory.dashBoardView.render();
      this.$content.html(directory.dashBoardView.el);
      directory.shellView.selectMenuItem('dashboard-menu');
   },

   testconfiguration: function () {
      "use strict";
      directory.testConfigurationView = new directory.TestConfigurationView();
      directory.testConfigurationView.render();
      this.$content.html(directory.testConfigurationView.el);
   },

   tests: function () {
      "use strict";
      directory.testScriptView = new directory.TestScriptsPageView();
      directory.testScriptView.render();
      this.$content.html(directory.testScriptView.el);
      directory.shellView.selectMenuItem('testscripts-menu');
   },

   targets: function () {
      "use strict";
      directory.testTargetView = new directory.TestTargetsPageView();
      directory.testTargetView.render();
      this.$content.html(directory.testTargetView.el);
      directory.shellView.selectMenuItem('applications-menu');
   },

   onLoggedOff: function () {
      "use strict";
      $.ajaxSetup({  
         headers : { 
            "Authorization" : null,            
         }
      });
      this.navigate("login", {trigger: true});
   },

   authCredentials: function (user) {
      "use strict";
      console.log("Provided creds:" + user.username);
      $.ajaxSetup({  
         headers : { 
            "Authorization" : "Basic " + CryptoJS.enc.Base64.stringify(CryptoJS.enc.Latin1.parse(user.username + ":" + user.password)),            
         }
      });
      console.log("login ok");
      directory.shellView.trigger('loginPageHide', 'ddd');
      this.navigate("", {trigger: true});
   },

   jobresult: function (id) {
      "use strict";
      directory.testResultView = new directory.TestRunResultView({testRunId: id});
      directory.testResultView.render();
      directory.testResultView = new Backbone.BootstrapModal({
         content: directory.testResultView,
         title: directory.testResultView.getTitle(),
         animate: true,
         allowCancel: false
      }).open();
   },

   device: function(id) {
       "use strict";
       directory.device = new directory.Device({ id: id });      
       directory.deviceView = new directory.DeviceView({model: directory.device});      
       this.$content.html(directory.deviceView.el);
   }
});

$(document).on("ready", function () {
   "use strict";
   directory.loadTemplates([
      "DashBoardView",
      "ShellView",
      "LoginPageView",
      "DeviceListView",
      "DeviceListItemView",
      "TestConfigurationListView",
      "TestConfigurationListItemView",
      "TestRunListView",
      "TestRunListItemView",
      "TestConfigurationView",
      "DeviceTypeListView",
      "DeviceTypeListItemView",
      "TestScriptSelectView",
      "TestTargetSelectView",
      "TestScriptsPageView",
      "TestScriptListView",
      "TestScriptListItemView",
      "TestTargetsPageView",
      "TestTargetListView",
      "TestTargetListItemView",
      "JobResultView",
      "PaginatedView",
      "TestRunResultView",
      "DeviceView",
      "DeviceShareListView",
      "DeviceShareListItemView",
      "RegistrationPageView"
   ],

         function () {
            // Load other small templates
            $.get('tpl/OtherTemplates.html', function (data) {
               directory.templateAlert = _.template($(data).filter("#alertTemplate").html());
            });
            directory.user = {name: "notset"};
            directory.router = new directory.Router();
            Backbone.history.start();
         });
});

$.ajaxSetup({
   statusCode: {
      401: function () {
         "use strict";
         // Redirec the to the login page.
         window.location.replace('/#login');

      },
      403: function () {
         "use strict";
         // 403 -- Access denied
         window.location.replace('/#denied');
      }
   }
});