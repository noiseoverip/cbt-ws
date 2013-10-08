directory.ShellView = Backbone.View.extend({   

    events: {        
        "click #logoff" : "logOff",       
    },
    
    initialize: function() {
        this.on("loginPageHide", this.showNavBar, this);
        this.on("loginPageShow", this.hideNavBar, this);
    },

    hideNavBar: function() {        
        this.$(".navbar").hide();
    },

    showNavBar: function() {        
        this.$(".navbar").show();
    },

    render: function () {
        this.$el.html(this.template({user: directory.user}));  
        return this;
    },     

    onkeypress: function (event) {
        if (event.keyCode === 13) { // enter key pressed
            event.preventDefault();
        }
    },

    selectMenuItem: function(menuItem) {
        $('.navbar ul li').removeClass('active');
        if (menuItem) {
            $('.' + menuItem).addClass('active');
        }
    },

    showAlert : function(alertClass, message) {
        this.$el.find("#alerts").append(
            directory.templateAlert({alertClass : alertClass, message : message})
        );
    },

    logOff: function() {
        directory.router.trigger('loggedOff',"");
    }
});

directory.LoginPageView = Backbone.View.extend({

    events: {
        "click #submitLogin" : "tryLogin",       
    },

    render: function () {       
        this.$el.html(this.template());          
        return this;
    },

    tryLogin: function(e) {       
        document.cookie="auth=" + this.$('#username').val() + ":" + md5(this.$('#password').val());        
        directory.router.navigate("", {trigger: true});
        e.preventDefault(); // prevent default button handling
        directory.router.trigger('loginSuccess','ddd');
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