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

* Map ResultSet to Entity
![Alt text](screenshots/6.PNG?raw=true)

### Use


## Screenshots

Certification <br />
![Alt text](screenshots/1.PNG?raw=true)