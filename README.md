JAJB
===
Java Architecture for JSON Binding

JAJB provides the simplest, shortest way to call the server side java api from the front end javascript.

## First step

1. Create a POJO Service.

```java
package foo;

import com.d_project.jajb.rpc.Callable;

public class MyService {
  @Callable
  public int add(int a, int b) { return a + b; }
}
```

2. Setup a RPCServlet.

/WEB-INF/web.xml
```xml
  <servlet>
    <servlet-name>jajb-rpc</servlet-name>
    <servlet-class>com.d_project.jajb.rpc.RPCServlet</servlet-class>
    <init-param>
      <param-name>services</param-name>
      <param-value>/WEB-INF/services.properties</param-value>
    </init-param>
  </servlet>

  <servlet-mapping>
    <servlet-name>jajb-rpc</servlet-name>
    <url-pattern>/jajb-rpc</url-pattern>
  </servlet-mapping>
```

/WEB-INF/services.properties
```properties
MyService=foo.MyService
```

3. Call from a client(with jQuery).

```javascript
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
```

Here is a result.

```
{"status":"success","result":8}
```

## With POJO VO (Value Object)

```java
package foo;

import com.d_project.jajb.JSONSerializable;

@JSONSerializable
public class MyVO {

  @JSONSerializable
  private String message;
  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }

  // This property will not be serialized.
  private int iamPrivate;
  public int getIamPrivate() { return iamPrivate; }
  public void setIamPrivate(int iamPrivate) { this.iamPrivate = iamPrivate; }
}
```

```java
package foo;

import com.d_project.jajb.rpc.Callable;

public class MyVOService {
  @Callable
  public MyVO helloVO(MyVO vo) {
    vo.setMessage("hello," + vo.getMessage() );
    vo.setIamPrivate(123);
    return vo;
  }
}
```

```javascript
  $.ajax({
    url : 'jajb-rpc',
    method : 'POST',
    type : 'application/json',
    data : JSON.stringify([
      { serviceName : 'MyVOService', methodName : 'helloVO' }, // opts
      [ { message : 'abc', iamPrivate : 'secret' } ] // args
    ])
  }).done(function(data) {
    console.log(JSON.stringify(data) );
  });
```

Here is a result.

```
{"status":"success","result":{"message":"hello,abc"}}
```
