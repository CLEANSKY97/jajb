$(function() {

  $.ajax({
    url : 'jajb-rpc',
    method : 'POST',
    type : 'application/json',
    data : JSON.stringify([
      { serviceName : 'MyService', methodName : 'add' }, // opts
      [ 3, 5 ] // args
    ])
  }).done(function(data) {
    console.log(JSON.stringify(data) );
  });

  $.ajax({
    url : 'jajb-rpc',
    method : 'POST',
    type : 'application/json',
    data : JSON.stringify([
      { serviceName : 'MyVOService', methodName : 'helloVO' }, // opts
      [ { message : 'abc', notSerializable : 'will be ignored' } ] // args
    ])
  }).done(function(data) {
    console.log(JSON.stringify(data) );
  });

});
