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

| JSON Type              | Java Type                   |
| ---------------------- | --------------------------- |
| Number                 | Primitive Number Types      |
| Array                  | Array, java.util.ArrayList  |
| Object                 | POJO Object                 |

