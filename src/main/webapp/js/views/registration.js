directory.RegistrationPageView = Backbone.View.extend({

   events: {
      "click #submit": "register",
   },

   render: function () {
      this.$el.html(this.template());

      this.$("#registrationForm").validate({
         rules : {
            username: {
               required: true,
               minlength: 2,
            },
            password: {
               required: true,
               minlength: 5
            },
            confirm_password: {
               required: true,
               minlength: 5,
               equalTo: "#password"
            },
            email: {
               email: true,
               required: true,
            }
         },
         messages: {
               username: {
               required: "Please enter a username",
               minlength: "Your username must consist of at least 2 characters"
            },
            password: {
               required: "Please provide a password",
               minlength: "Your password must be at least 5 characters long"
            },
            confirm_password: {
               required: "Please provide a password",
               minlength: "Your password must be at least 5 characters long",
               equalTo: "Please enter the same password as above"
            },
            email: "Please enter a valid email address"
         }
      });
      return this;
   },

   register: function (e) {
      var username = this.$("#username").val();
      var password = this.$("#password").val();
      var email = this.$("#email").val();
      CbtClient.createUser(username, password, email,
         // Success
         function() {
            directory.router.navigate("", {trigger: true});
         }, 
         // Failure
         function(message) {
            alert("Registration failed:  " + message);
         }
      );
      e.preventDefault(); // prevent default button handling      
   }  
});