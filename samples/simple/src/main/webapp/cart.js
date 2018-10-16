
var vm = new Vue({
  el : '#myapp',
  mounted : function() {
    this.cartService('newCart')().done(function(data) {
      this.cartVO = data.result;
    });
  },
  data : {
    cartVO : null
  },
  methods : {
    cartService : jajb.getService('CartService'),
    add_clickHandler : function() {
      this.cartService('addItem')(this.cartVO).done(function(data) {
        this.cartVO = data.result;
      });
    },
    remove_clickHandler : function() {
      this.cartService('removeItems')(this.cartVO).done(function(data) {
        this.cartVO = data.result;
      });
    }
  }
});
