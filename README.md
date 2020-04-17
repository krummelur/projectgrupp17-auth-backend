# projectgrupp17-auth-backend
auth service for iot project using JWT
implemented with spring testing with mockito

![Java CI with Gradle](https://github.com/krummelur/projectgrupp17-auth-backend/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=master)


## endpoints

* / (get) 

get api version.

```
```

* /register/    (post)

Registers a new user, body:

```
{
username: <username>,
email: <email>,
password: <password>,
agency: <an existing agency>
}
```

* auth/login (post)

Logs in an existing user, body:

```
{
email: <email>,
password: <password>,
}
```

* auth/logout (post)

Logs out an existing user, destroying the refresh token, headers:

* Auth-Token: <JWT-access-token>,
* Refresh-Token: <refresh-token-id>

```
```
* auth/refresh

Gets a new access-token from an expired access-token and non-expired refresh-token, headers:

* Auth-Token: <JWT-access-token>,
* Refresh-Token: <refresh-token-id>

```
```
# build
* gradle build

## Environment
* AUTH_IOT_ENVIRONMENT: PRODUCTION|TEST 
#### production
* SQL_USERNAME: username for database
* SQL_PASSWORD: password for database
* SQL_HOST: mysql host
* SQL_DB_NAME: database name
