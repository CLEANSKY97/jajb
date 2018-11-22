
var vm = new Vue({
  el : '#myapp',
  data : {
    cartVO : null,
    modal : {
      show : false,
      message : '',
      title : ''
    }
  },
  mounted : function() {
    this.cartService('newCart')().done(function(data) {
      this.cartVO = data.result;
    });
  },
  methods : {
    cartService : jajb.getService('CartService'),
    alert : function(message, title) {
      this.modal.title = title;
      this.modal.message = message;
      this.modal.show = true;
    },
    add_clickHandler : function() {
      this.cartService('addItem')(this.cartVO).done(function(data) {
        this.cartVO = data.result;
      }).fail(function(data) {
        this.alert(data.message);
      });
    },
    remove_clickHandler : function() {
      this.cartService('removeItems')(this.cartVO).done(function(data) {
        this.cartVO = data.result;
      }).fail(function(data) {
        this.alert(data.message);
      });
    }
  }
});
