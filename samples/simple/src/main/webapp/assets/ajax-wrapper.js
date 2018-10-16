//
// This sample wraps jQuery's ajax and enables calling server-side service.
//

var jajb = function() {

  var loginVO = null;

  var getService = function(serviceName, serviceOpts) {
    return function(methodName, methodOpts) {
      var _this = this;
      var opts = { showBlock : true };
      return function() {

        var callbacks = {
          done : null,
          fail : null,
          always : null
        };
        var emit = function(type, data) {
          if (callbacks[type]) {
            callbacks[type].call(_this, data);
          }
        };

        var args = [];
        for (var i = 0; i < arguments.length; i += 1) {
          args.push(arguments[i]);
        }
        var data = [{ serviceName : serviceName,
            methodName : methodName }, args ];

        var headers = {};
        if (loginVO) {
          headers['X-USER-ID'] = encodeURIComponent(loginVO.userId);
          headers['X-AUTH-TOKEN'] = loginVO.authToken;
        }

        $.ajax({
          type : 'POST',
          url : 'jajb-rpc',
          contentType : 'application/json',
          data : JSON.stringify(data),
          headers : headers
        }).done(function(data) {
          if (data.status === 'success') {
            emit('done', data);
          } else if (data.status === 'failure') {
            emit('fail', data);
          } else {
            emit('done', data);
          }
        }).fail(function(data) {
          emit('fail', { status : 'failure', message : 'system-error' });
        }).always(function(data) {
          emit('always', data);
        });

        return {
          done : function(done) {
            callbacks.done = done;
            return this;
          },
          fail : function(fail) {
            callbacks.fail = fail;
            return this;
          },
          always : function(always) {
            callbacks.always = always;
            return this;
          }
        };
      };
    };
  };

  return {
    getService : getService
  };

}();
