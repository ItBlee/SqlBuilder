# Automatic SqlBuilder

### Introduction
A library supports process automation and reduces build time in the DAO layer.

### Technologies Used
Java Reflection, Java Generic, Java Annotation, JDBC

### List of features
* Auto validate and cast parameters type.
* Auto map parameters with declared Keys.
* Auto build SQL statements based on parameters type and Keys.
* Format and Execute SQL statements.
* Auto extract and map ResultSet to Entity.

## Example

* Request (GET)
![Alt text](screenshots/7.PNG?raw=true)

* Generate
```sql
    SELECT
        building.id,
        building.name,
        building.street,
        building.ward,
        building.floorarea,
        building.rentprice,
        building.brokeragefee,
        building.managername,
        building.managerphone,
        building.createddate,
        district.id AS "district.Id",
        district.code AS "district.Code",
        district.name AS "district.Name",
        rentarea.id AS "rentarea.Id",
        rentarea.value AS "rentarea.Value",
        renttype.id AS "renttype.Id",
        renttype.code AS "renttype.Code",
        renttype.name AS "renttype.Name",
        user.id AS "user.Id",
        user.fullname AS "user.FullName",
        building.districtid 
    FROM
        building 
    LEFT JOIN
        district 
            ON building.districtid = district.id 
    LEFT JOIN
        rentarea 
            ON building.id = rentarea.buildingid 
    LEFT JOIN
        buildingrenttype 
            ON building.id = buildingrenttype.buildingid 
    LEFT JOIN
        renttype 
            ON renttype.id = buildingrenttype.renttypeid 
    LEFT JOIN
        assignmentbuilding 
            ON building.id = assignmentbuilding.buildingid 
    LEFT JOIN
        (
            SELECT
                user.id,
                user.fullname 
            FROM
                user 
            INNER JOIN
                user_role 
                    ON user.id = user_role.userid 
            INNER JOIN
                role 
                    ON role.id = user_role.roleid
            ) AS user 
                ON user.id = assignmentbuilding.staffid 
        WHERE
            (
                renttype.code LIKE '%tang-tret%' 
                OR renttype.code LIKE '%nguyen-can%'
            ) 
            AND EXISTS (
                SELECT
                    * 
                FROM
                    rentarea 
                WHERE
                    (
                        rentarea.buildingid = building.id  
                        AND rentarea.value BETWEEN 100 AND 150  
                    )
            ) 
            AND building.floorarea = 500 
            AND district.code = 'Q4' 
            AND building.name LIKE '%ACM%' 
            AND user.id = 2
```

* Map ResultSet to Entity <br />
![Alt text](screenshots/6.PNG?raw=true)

### Use

* First, implement the SqlKey with enum
SqlKey structure
```java
public enum AbcKey implements SqlKey {
    private final String param;
    private final Class<?> fieldType;
    private final SqlStatement statement;

    AbcKey(SqlStatement statement) {
        if (statement == null)
            throw new IllegalArgumentException();
        this.param = "";
        this.fieldType = Object.class;
        this.statement = statement;
    }

    AbcKey(String param, Class<?> fieldType, SqlStatement statement) {
        StringUtils.requireNonBlank(param);
        if (fieldType == null)
            throw new IllegalArgumentException();
        this.param = param;
        this.fieldType = fieldType;
        this.statement = statement;
    }

    @Override
    public SqlStatement getStatement() {
        return statement;
    }

    @Override
    public String getParamName() {
        return param;
    }

    @Override
    public Class<?> getType() {
        return fieldType;
    }

    @Override
    public boolean isScope() {
        return StringUtils.isBlank(getParamName())
                && getType() == Object.class
                && statement != null;
    }
}
```

* Declare SqlKey with EXAMPLE <br />
![Alt text](screenshots/11.PNG?raw=true)


* Then create SqlMap<Key> with LinkedSqlMap implement
    - Add Scope if u need to set base statement
    - put to Map with key is SqlKey and value is parameter value
    - or **auto put** with putAll method require Map<String, ?> and SqlKey type
![Alt text](screenshots/9.PNG?raw=true)

* Create SqlBuilderFactory based on action description(query, insert, update, delete, count, ...)
    - get SqlBuilder from Factory then generate sql with build() call
    - get SqlExecutor from Factory then execute sql with executeQuery(sql, Entity.class)
![Alt text](screenshots/8.PNG?raw=true)
