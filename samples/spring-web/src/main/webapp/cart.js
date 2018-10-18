
var vm = new Vue({
  el : '#myapp',
  data : {
    cartVO : null
  },
  mounted : function() {
    this.cartService('newCart')().done(function(data) {
      this.cartVO = data.result;
    });
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
