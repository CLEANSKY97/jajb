JAJB
===
Java Architecture for JSON Binding


1. Create POJO Service.

```java
package foo;

public class MyService {
  @JSONSerializable
  public int add(int a, int b) { return a + b; }
}
```

2. Prepare RPCServlet.

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
  <url-mapping>/jajb-rpc</url-mapping>
</servlet-mapping>
```

/WEB-INF/services.properties
```properties
MyService=foo.MyService
```

3. Call from client.

```javascript
$.ajax({
  
}).done(
);
```
