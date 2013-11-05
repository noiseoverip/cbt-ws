directory.LoginPageView = Backbone.View.extend({

   events: {
      "click #submitLogin": "tryLogin",
      "click #register": "sendToRegister",
   },

   render: function () {
      this.$el.html(this.template());
      return this;
   },

   tryLogin: function (e) {
      directory.router.trigger('authCredentials', {username : this.$('#username').val(), password: this.$('#password').val()});
      e.preventDefault(); // prevent default button handling
   },
   
   sendToRegister : function() {
      directory.router.navigate("register", {trigger: true});
   },

   getCookieValue: function (key) {
      currentcookie = document.cookie;
      if (currentcookie.length > 0) {
         firstidx = currentcookie.indexOf(key + "=");
         if (firstidx != -1) {
            firstidx = firstidx + key.length + 1;
            lastidx = currentcookie.indexOf(";", firstidx);
            if (lastidx == -1) {
               lastidx = currentcookie.length;
            }
            return unescape(currentcookie.substring(firstidx, lastidx));
         }
      }
      return "";
   }
});