directory.LoginPageView = Backbone.View.extend({

   events: {
      "click #submitLogin": "tryLogin",
   },

   render: function () {
      this.$el.html(this.template());
      return this;
   },

   tryLogin: function (e) {
      document.cookie = "auth=" + this.$('#username').val() + ":" + md5(this.$('#password').val());
      directory.router.navigate("", {trigger: true});
      e.preventDefault(); // prevent default button handling
      directory.router.trigger('loginSuccess', 'ddd');
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