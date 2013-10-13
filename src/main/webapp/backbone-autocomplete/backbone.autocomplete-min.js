var AutocompleteItemView = Backbone.View.extend({tagName: "li", template: '<a href="#"><%= label %></a>', events: {click: "select"}, render: function () {
   this.$el.html(_.template(this.template, {label: this.model.label()}));
   return this
}, select: function () {
   this.options.parent.hide().select(this.model);
   return false
}});
var AutocompleteView = Backbone.View.extend({tagName: "ul", itemView: AutocompleteItemView, className: "autocomplete", wait: 300, queryParameter: "query", currentText: "", initialize: function (A) {
   _.extend(this, A);
   this.filter = _.debounce(this.filter, this.wait)
}, render: function () {
   this.input.attr("autocomplete", "off");
   this.$el.width(this.input.outerWidth());
   this.input.keyup(this.keyup.bind(this)).keydown(this.keydown.bind(this)).after(this.$el);
   return this
}, keydown: function () {
   if (event.keyCode == 38) {
      return this.move(-1)
   }
   if (event.keyCode == 40) {
      return this.move(+1)
   }
   if (event.keyCode == 13) {
      return this.onEnter()
   }
   if (event.keyCode == 27) {
      return this.hide()
   }
}, keyup: function () {
   var A = this.input.val();
   if (this.isChanged(A)) {
      if (this.isValid(A)) {
         this.filter(A)
      } else {
         this.hide()
      }
   }
}, filter: function (A) {
   if (this.model.url) {
      var B = {};
      B[this.queryParameter] = A;
      this.model.fetch({success: function () {
         this.loadResult(this.model.models, A)
      }.bind(this), data: B})
   } else {
      this.loadResult(this.model.filter(function (C) {
         return C.label().indexOf(A) > -1
      }), A)
   }
}, isValid: function (A) {
   return A.length > 2
}, isChanged: function (A) {
   return this.currentText != A
}, move: function (A) {
   var C = this.$el.children(".active"), D = this.$el.children(), B = C.index() + A;
   if (D.eq(B).length) {
      C.removeClass("active");
      D.eq(B).addClass("active")
   }
   return false
}, onEnter: function () {
   this.$el.children(".active").click();
   return false
}, loadResult: function (B, A) {
   this.currentText = A;
   this.show().reset();
   if (B.length) {
      _.forEach(B, this.addItem, this);
      this.show()
   } else {
      this.hide()
   }
}, addItem: function (A) {
   this.$el.append(new this.itemView({model: A, parent: this}).render().$el)
}, select: function (B) {
   var A = B.label();
   this.input.val(A);
   this.currentText = A
}, reset: function () {
   this.$el.empty();
   return this
}, hide: function () {
   this.$el.hide();
   return this
}, show: function () {
   this.$el.show();
   return this
}});