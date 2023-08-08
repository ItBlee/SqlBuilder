package com.itblee.key;

import com.itblee.SqlKey;
import com.itblee.SqlStatement;
import com.itblee.model.*;
import com.itblee.util.StringUtils;

//EXAMPLE KEY
public enum BuildingKey implements SqlKey {

    ID ("id", Long.class, SqlQuery.builder()
        .select("building.id")
        .from("building")
        .where("building.id").build()
    ),

    NAME ("name", String.class, SqlQuery.builder()
        .select("building.name")
        .from("building")
        .where("building.name").build()
    ),

    DISTRICT_CODE ("districtcode", Code.class, SqlQuery.builder()
        .select("district.id AS \"district.Id\"",
                "district.code AS \"district.Code\"",
                "district.name AS \"district.Name\"",
                "building.districtid")
        .from("building")
        .joinWith(SqlJoin.builder()
            .type(SqlJoin.Type.INNER_JOIN)
            .join("district")
            .on("building.districtid = district.id").done())
        .where("district.code").build()
    ),

    WARD ("ward", String.class, SqlQuery.builder()
        .select("building.ward")
        .from("building")
        .where("building.ward").build()
    ),

    LEVEL ("level", String.class, SqlQuery.builder()
        .select("building.level")
        .from("building")
        .where("building.level").build()
    ),

    STREET ("street", String.class, SqlQuery.builder()
        .select("building.street")
        .from("building")
        .where("building.street").build()
    ),

    DIRECTION ("direction", String.class, SqlQuery.builder()
        .select("building.direction")
        .from("building")
        .where("building.direction").build()
    ),

    RENT_PRICE("rentprice", Range.class, SqlQuery.builder()
        .select("building.rentprice")
        .from("building")
        .where("building.rentprice").build()
    ),

    FLOOR_AREA ("floorarea", Integer.class, SqlQuery.builder()
        .select("building.floorarea")
        .from("building")
        .where("building.floorarea").build()
    ),

    MANAGER_NAME ("managername", String.class, SqlQuery.builder()
        .select("building.managername")
        .from("building")
        .where("building.managername").build()
    ),

    MANAGER_PHONE ("managerphone", String.class, SqlQuery.builder()
        .select("building.managerphone")
        .from("building")
        .where("building.managerphone").build()
    ),

    NUMBER_OF_BASEMENT ("numberofbasement", Integer.class, SqlQuery.builder()
        .select("building.numberofbasement")
        .from("building")
        .where("building.numberofbasement").build()
    ),

    /*RENT_AREA ("rentarea", Range.class, SqlQuery.builder()
        .select("rentarea.id AS \"rentarea.Id\"",
                "rentarea.value AS \"rentarea.Value\"")
        .from("building")
        .joinWith(SqlJoin.builder()
            .type(SqlJoin.Type.INNER_JOIN)
            .join("rentarea")
            .on("building.id = rentarea.buildingid").done())
        .where("rentarea.value").build()
    ),*/

    RENT_AREA ("rentarea", Range.class, SqlQuery.builder()
            .select("rentarea.id AS \"rentarea.Id\"",
                    "rentarea.value AS \"rentarea.Value\"")
            .from("building")
            .where(SqlQuery.builder()
                    .select("*")
                    .from("rentarea")
                    .whereWithoutValue("rentarea.buildingid = building.id")
                    .where("rentarea.value")
                    .build()
            ).build()
    ),

    RENT_TYPES ("types", CodeSet.class, SqlQuery.builder()
        .select("renttype.id AS \"renttype.Id\"",
                "renttype.code AS \"renttype.Code\"",
                "renttype.name AS \"renttype.Name\"")
        .from("building")
        .joinWith(
            SqlJoin.builder()
            .type(SqlJoin.Type.INNER_JOIN)
            .join("buildingrenttype")
            .on("building.id = buildingrenttype.buildingid").done(),

            SqlJoin.builder()
            .type(SqlJoin.Type.INNER_JOIN)
            .join("renttype")
            .on("renttype.id = buildingrenttype.renttypeid").done()
        )
        .where("renttype.code").build()
    ),

    STAFF ("staffid", Long.class, SqlQuery.builder()
        .select("user.id AS \"user.Id\"",
                "user.fullname AS \"user.FullName\"")
        .from("building")
        .joinWith(
            SqlJoin.builder()
            .type(SqlJoin.Type.INNER_JOIN)
            .join("assignmentbuilding")
            .on("building.id = assignmentbuilding.buildingid").done(),

            SqlJoin.builder()
            .type(SqlJoin.Type.INNER_JOIN)
            .join(
                SqlQuery.builder()
                .select("user.id", "user.fullname")
                .from("user")
                .joinWith(
                    SqlJoin.builder()
                    .type(SqlJoin.Type.INNER_JOIN)
                    .join("user_role")
                    .on("user.id = user_role.userid").done(),

                    SqlJoin.builder()
                    .type(SqlJoin.Type.INNER_JOIN)
                    .join("role")
                    .on("role.id = user_role.roleid").done()
                )
                .where("role.code = \"staff\"")
                .as("user").build()
            )
            .on("user.id = assignmentbuilding.staffid").done()
        )
        .where("user.id").build()
    ),

    //SCOPE
    COUNT (SqlQuery.builder()
        .select("count(DISTINCT building.id) AS count")
        .from("building").build()
    ),

    //SCOPE
    ALL (SqlQuery.builder()
        .select("building.id",
                "building.name",
                "building.street",
                "building.ward",
                "building.structure",
                "building.numberofbasement",
                "building.floorarea",
                "building.direction",
                "building.level",
                "building.rentprice",
                "building.rentpricedescription",
                "building.servicefee",
                "building.carfee",
                "building.motorbikefee",
                "building.overtimefee",
                "building.waterfee",
                "building.electricityfee",
                "building.deposit",
                "building.payment",
                "building.renttime",
                "building.decorationtime",
                "building.brokeragefee",
                "building.managername",
                "building.managerphone",
                "building.note",
                "building.linkofbuilding",
                "building.map",
                "building.image",
                "building.createddate",
                "building.createdby",
                "building.modifieddate",
                "building.modifiedby",
                "district.id AS \"district.Id\"",
                "district.code AS \"district.Code\"",
                "district.name AS \"district.Name\"",
                "rentarea.id AS \"rentarea.Id\"",
                "rentarea.value AS \"rentarea.Value\"",
                "renttype.id AS \"renttype.Id\"",
                "renttype.code AS \"renttype.Code\"",
                "renttype.name AS \"renttype.Name\"",
                "user.id AS \"user.Id\"",
                "user.fullname AS \"user.FullName\""
        )
        .from("building")
        .joinWith(SqlJoin.builder()
            .type(SqlJoin.Type.LEFT_JOIN)
            .join("district")
            .on("building.districtid = district.id").done(),

                SqlJoin.builder()
            .type(SqlJoin.Type.LEFT_JOIN)
            .join("rentarea")
            .on("building.id = rentarea.buildingid").done(),

                SqlJoin.builder()
            .type(SqlJoin.Type.LEFT_JOIN)
            .join("buildingrenttype")
            .on("building.id = buildingrenttype.buildingid").done(),

                SqlJoin.builder()
            .type(SqlJoin.Type.LEFT_JOIN)
            .join("renttype")
            .on("renttype.id = buildingrenttype.renttypeid").done(),

                SqlJoin.builder()
            .type(SqlJoin.Type.LEFT_JOIN)
            .join("assignmentbuilding")
            .on("building.id = assignmentbuilding.buildingid").done(),

                SqlJoin.builder()
            .type(SqlJoin.Type.LEFT_JOIN)
            .join(SqlQuery.builder()
                .select("user.id", "user.fullname")
                .from("user")
                .joinWith(SqlJoin.builder()
                    .type(SqlJoin.Type.INNER_JOIN)
                    .join("user_role")
                    .on("user.id = user_role.userid").done(),

                         SqlJoin.builder()
                    .type(SqlJoin.Type.INNER_JOIN)
                    .join("role")
                    .on("role.id = user_role.roleid").done())

                .where("role.code = \"staff\"")
                .as("user").build()
            )
            .on("user.id = assignmentbuilding.staffid").done()
        ).build()
    ),

    //SCOPE
    SHORTEN (SqlQuery.builder()
        .select("building.id",
                "building.name",
                "building.street",
                "building.ward",
                "building.floorarea",
                "building.rentprice",
                "building.brokeragefee",
                "building.managername",
                "building.managerphone",
                "building.createddate",
                "district.id AS \"district.Id\"",
                "district.code AS \"district.Code\"",
                "district.name AS \"district.Name\"",
                "rentarea.id AS \"rentarea.Id\"",
                "rentarea.value AS \"rentarea.Value\"",
                "renttype.id AS \"renttype.Id\"",
                "renttype.code AS \"renttype.Code\"",
                "renttype.name AS \"renttype.Name\"",
                "user.id AS \"user.Id\"",
                "user.fullname AS \"user.FullName\""
        )
        .from("building")
        .joinWith(SqlJoin.builder()
            .type(SqlJoin.Type.LEFT_JOIN)
            .join("district")
            .on("building.districtid = district.id").done(),

                SqlJoin.builder()
            .type(SqlJoin.Type.LEFT_JOIN)
            .join("rentarea")
            .on("building.id = rentarea.buildingid").done(),

                SqlJoin.builder()
            .type(SqlJoin.Type.LEFT_JOIN)
            .join("buildingrenttype")
            .on("building.id = buildingrenttype.buildingid").done(),

                SqlJoin.builder()
            .type(SqlJoin.Type.LEFT_JOIN)
            .join("renttype")
            .on("renttype.id = buildingrenttype.renttypeid").done(),

                SqlJoin.builder()
            .type(SqlJoin.Type.LEFT_JOIN)
            .join("assignmentbuilding")
            .on("building.id = assignmentbuilding.buildingid").done(),

                SqlJoin.builder()
            .type(SqlJoin.Type.LEFT_JOIN)
            .join(SqlQuery.builder()
                .select("user.id", "user.fullname")
                .from("user")
                .joinWith(SqlJoin.builder()
                    .type(SqlJoin.Type.INNER_JOIN)
                    .join("user_role")
                    .on("user.id = user_role.userid").done(),

                        SqlJoin.builder()
                    .type(SqlJoin.Type.INNER_JOIN)
                    .join("role")
                    .on("role.id = user_role.roleid").done())

                .where("role.code = \"staff\"")
                .as("user").build()
            )
            .on("user.id = assignmentbuilding.staffid").done()
        ).build()
    );

    private final String param;
    private final Class<?> fieldType;
    private final SqlStatement statement;

    BuildingKey(SqlStatement statement) {
        if (statement == null)
            throw new IllegalArgumentException();
        this.param = "";
        this.fieldType = Object.class;
        this.statement = statement;
    }

    /*BuildingKey(String param, Class<?> fieldType) {
        StringUtils.requireNonBlank(param);
        if (fieldType == null)
            throw new IllegalArgumentException();
        this.param = param;
        this.fieldType = fieldType;
        this.statement = null;
    }*/

    BuildingKey(String param, Class<?> fieldType, SqlStatement statement) {
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

    /*@Override
    public boolean isMarker() {
        return statement == null;
    }*/

}
