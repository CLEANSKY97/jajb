JAJB
===
Java Architecture for JSON Binding


1. Create POJO Service.

```java
package foo;

import com.d_project.jajb.rpc.Callable;

public class MyService {
  @Callable
  public int add(int a, int b) { return a + b; }
}
```

2. Setup RPCServlet.

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

3. Call from client.

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

A result in the console.

```
{"status":"success","result":8}
```
