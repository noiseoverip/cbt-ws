directory.ShellView = Backbone.View.extend({

   events: {
      "click #logoff": "logOff",
   },

   initialize: function () {
      this.on("loginPageHide", this.showNavBar, this);
      this.on("loginPageShow", this.hideNavBar, this);
   },

   hideNavBar: function () {
      this.$(".navbar").hide();
   },

   showNavBar: function () {
      this.$(".navbar").show();
   },

   render: function () {
      this.$el.html(this.template({
         user: directory.user,
         version: CbtClient.version,
         buildtime: CbtClient.buildtime
      }));
      return this;
   },

   onkeypress: function (event) {
      if (event.keyCode === 13) { // enter key pressed
         event.preventDefault();
      }
   },

   selectMenuItem: function (menuItem) {
      $('.navbar ul li').removeClass('active');
      if (menuItem) {
         $('.' + menuItem).addClass('active');
      }
   },

   showAlert: function (alertClass, message) {
      this.$el.find("#alerts").append(
            directory.templateAlert({alertClass: alertClass, message: message})
      );
   },

   logOff: function () {
      directory.router.trigger('loggedOff', "");
   }
});