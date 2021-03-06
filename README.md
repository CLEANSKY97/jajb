JAJB
===
Java Architecture for JSON Binding

JAJB provides the simplest, shortest way to call the server side java api
from the front end javascript.

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

3. Call from a client (with jQuery here).

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

```json
{"status":"success","result":8}
```

With [jquery-ajax-wrapper.js](https://raw.githubusercontent.com/kazuhikoarase/jajb/master/samples/simple/src/main/webapp/assets/jquery-ajax-wrapper.js), call more simply.

```javascript
  jajb.getService('MyService')('add')(3, 5).done(function(data) {
    console.log(JSON.stringify(data) );
  });
```

## With POJO VO (Value Object)

```java
package foo;

import com.d_project.jajb.JSONField;
import com.d_project.jajb.JSONType;

@JSONType
public class MyVO {

  @JSONField
  private String message;
  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }

  // This property will not be serialized.
  private String notSerializable;
  public String getNotSerializable() { return notSerializable; }
  public void setNotSerializable(String notSerializable) {
    this.notSerializable = notSerializable;
  }
}
```

```java
package foo;

import com.d_project.jajb.rpc.Callable;

public class MyVOService {
  @Callable
  public MyVO helloVO(MyVO vo) {
    vo.setMessage("hello," + vo.getMessage() +
        "," + vo.getNotSerializable() );
    vo.setNotSerializable("Can you hear me?");
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
      [ { message : 'abc', notSerializable : 'will be ignored' } ] // args
    ])
  }).done(function(data) {
    console.log(JSON.stringify(data) );
  });
```

Here is a result.

```json
{"status":"success","result":{"message":"hello,abc,null"}}
```

## Type Mappings

### Java to JSON

| Java Type              | JSON Type     |
| ---------------------- | ------------- |
| null (Object)          | null (Object) |
| boolean                | Boolean       |
| Primitive Number Types | Number        |
| java.lang.Number       | Number        |
| java.lang.String       | String        |
| Array                  | Array         |
| java.util.Iterable     | Array         |
| java.util.Map          | Object        |
| POJO Object            | Object        |

### JSON to Java (basic)

| JSON Type              | Java Type               |
| ---------------------- | ----------------------- |
| null (Object)          | null (Object)           |
| Boolean                | boolean                 |
| Number                 | java.math.BigDecimal    |
| String                 | java.lang.String        |
| Array                  | java.util.ArrayList     |
| Object                 | java.util.LinkedHashMap |

### JSON to Java (with auto cast by reflection)

| JSON Type              | Java Type                                    |
| ---------------------- | -------------------------------------------- |
| Number                 | Primitive Number Types, java.math.BigInteger |
| Array                  | Array, java.util.ArrayList                   |
| Object                 | POJO Object                                  |
