package com.fredriksonsound.iot_backoffice_auth;

/**
 * General app environment settings. Obsolete since @PropertySource can do this, in appConfig.
 */
public class Environment {
    private static final String PROD_STR = "PRODUCTION";
    private static final String ENV_STR = "AUTH_ENVIRONMENT";
    private static final String SQL_HOST_STR = "SQL_HOST";
    private static final String SQL_DB_NAME_STR = "SQL_DB_NAME";
    private static final String SQL_PASS_STR = "SQL_PASSWORD";
    private static final String SQL_USR_STR = "SQL_USERNAME";
    public static final String JWT_PUB_KEY_STR = "JWT_PUB_KEY";
    public static final String JWT_PRIV_KEY_STR = "JWT_PRIV_KEY";
    private static final String TEST = "_TEST";

    public final String SQL_HOST;
    public final String SQL_DB;
    public final String SQL_PASS;
    public final String SQL_USER;
    public final String ENVIRONMENT;
    public Environment() {
        boolean prod = false;
        ENVIRONMENT = System.getenv(ENV_STR);
        if(ENVIRONMENT.equals(PROD_STR))
            prod = true;
        SQL_HOST = System.getenv(prod ? SQL_HOST_STR : SQL_HOST_STR+TEST);
        SQL_DB = System.getenv(prod ? SQL_DB_NAME_STR :  SQL_DB_NAME_STR+TEST );
        SQL_PASS = System.getenv(prod ? SQL_PASS_STR :  SQL_PASS_STR+TEST );
        SQL_USER = System.getenv(prod ? SQL_USR_STR:  SQL_USR_STR+TEST );
    }
}
