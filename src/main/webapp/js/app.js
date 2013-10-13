var directory = {

   views: {},

   models: {},

   loadTemplates: function (views, callback) {

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
      "": "home",
      "login": "login",
      "testconfiguration": "testconfiguration",
      "testscripts": "tests",
      "applications": "targets"
   },

   initialize: function () {
      this.on("loginSuccess", this.loginSuccess, this);
      this.on("loggedOff", this.onLoggedOff, this);

      directory.shellView = new directory.ShellView();
      $('body').html(directory.shellView.render().el);
      this.$content = $("#content");
   },

   login: function () {
      directory.loginView = new directory.LoginPageView();
      directory.loginView.render();
      this.$content.html(directory.loginView.el);
      directory.shellView.trigger('loginPageShow', 'ddd');
   },

   home: function () {
      directory.devicePageView = new directory.DevicePageView();
      directory.devicePageView.render();
      this.$content.html(directory.devicePageView.el);
      directory.shellView.selectMenuItem('home-menu');
   },

   testconfiguration: function () {
      directory.testConfigurationView = new directory.TestConfigurationView();
      directory.testConfigurationView.render();
      this.$content.html(directory.testConfigurationView.el);
   },

   tests: function () {
      directory.testScriptView = new directory.TestScriptsPageView();
      directory.testScriptView.render();
      this.$content.html(directory.testScriptView.el);
      directory.shellView.selectMenuItem('testscripts-menu');
   },

   targets: function () {
      directory.testTargetView = new directory.TestTargetsPageView();
      directory.testTargetView.render();
      this.$content.html(directory.testTargetView.el);
      directory.shellView.selectMenuItem('applications-menu');
   },

   onLoggedOff: function () {
      document.cookie = 'auth=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
      directory.router.navigate("login", {trigger: true});
   },

   loginSuccess: function () {
      console.log("login ok");
      directory.shellView.trigger('loginPageHide', 'ddd');
   }

});

$(document).on("ready", function () {
   directory.loadTemplates([
      "ShellView",
      "LoginPageView",
      "HomeView",
      "DevicePageView",
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
      "TestTargetListItemView"
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
         // Redirec the to the login page.
         window.location.replace('/#login');

      },
      403: function () {
         // 403 -- Access denied
         window.location.replace('/#denied');
      }
   }
});