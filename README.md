# spring-security-acct-services
SpringBoot Security/Data RESTful api used for managing employee payrolls. 

# Usage

- Has different endpoints for managing and getting info from users (employees) and their salary/payrolls.

- Uses SpringBoot security/data/web for all its needs, be it database, security or web related. 
Will also handle everything related to the users authorization and authentication throughout the whole api (http basic auth).

- Logs all relevant events to the application database.

- Handles and creates logs when user trying to access endpoint is unauthorized or forbidden.

- HTTPS secured (self signed certificate for testing purposes only).

# Examples

<details>
<summary>POST: /api/auth/signup</summary>

POST a Json body to sign up a given user.

- Will check for all kinds of user validity and return exceptions and responses as needed.

- First user to be signed up to the system will always be of role administrator, all others will be of role user (roles can be managed through another endpoint).

- Passwords will always be encoded (via bcrypt encoder, so always hashed and salted).

- Only @acme.com emails are accepted as valid.

- Creates logs that user was created if sucessful.

Request body format:
```json
{
   "name": "Mauricio",
   "lastname": "Honorato",
   "email": "MauricioHonorato@acme.com",
   "password": "mauriciospassword123"
}
```

Response body format (Http status: 200):
```json
{
    "id": 3602,
    "name": "Mauricio",
    "lastname": "Honorato",
    "email": "mauriciohonorato@acme.com",
    "roles": [
        "ROLE_ADMINISTRATOR"
    ]
}
```

Some exception examples:

When user in request json already exists.
```json
{
    "timestamp": "2023-09-11T00:58:55.244+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "User exist!",
    "path": "/api/auth/signup"
}
```

When request has missing or invalid parameters:
```json
{
    "timestamp": "2023-09-11T01:01:05.985+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid or missing user params!!",
    "path": "/api/auth/signup"
}
```

  
</details>

<details>
<summary>POST: /api/auth/changepass</summary>

POST a Json body to send a change password request. Will check for correct authentication (http basic auth) and password validity before persisting changes and returning the appropriate response.

- Password has to be atleast 12 chars long.

- Creates logs that user has changed password.

Request body format:
```json
{
   "new_password": "mauriciosnewpassword123"
}
```

Response body (Http status: 200):
```json
{
    "status": "The password has been updated successfully",
    "email": "mauriciohonorato@acme.com"
}
```

Some exception examples:


When user fails authentication:
```json
{
    "timestamp": "2023-09-11T01:10:13.998+00:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Bad credentials",
    "path": "/api/auth/changepass"
}
```

When user enters same password as current one:
```json
{
    "timestamp": "2023-09-11T01:12:39.177+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "The passwords must be different!",
    "path": "/api/auth/changepass"
}
```

When user enters invalid password:
```json
{
    "timestamp": "2023-09-11T01:13:01.619+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Password length must be 12 chars minimum!",
    "path": "/api/auth/changepass"
}
```
  
</details>

<details>
<summary>POST: /api/acct/payments</summary>

POST a Json body to send a request containing payment records to be upserted and persisted in the database.

- Will validate payments (period and salary validity check).

- Only user of role accountant is authorized to access this endpoint.

Request body:
```json
[
    {
        "employee": "eduardomacedo@acme.com",
        "period": "01-2021",
        "salary": 210233
    },
    {
        "employee": "mauriciohonorato@acme.com",
        "period": "01-2021",
        "salary": 123456
    },
    {
        "employee": "eduardomacedo@acme.com",
        "period": "02-2021",
        "salary": 221523
    }
]
```

Response body (Http status: 200):
```json
{
    "status": "Added successfully!"
}
```

Some exception examples:


When request body has any payment in invalid format:
```json
{
    "timestamp": "2023-09-11T01:24:36.016+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid payment format request body.",
    "path": "/api/acct/payments"
}
```

When request body has payment roll containing non existing employee:
```json
{
    "timestamp": "2023-09-11T01:25:18.635+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid employee email!",
    "path": "/api/acct/payments"
}
```

When authenticated user is not an accountant:
```json
{
    "timestamp": "2023-09-11T01:27:18.882+00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Access Denied!",
    "path": "/api/acct/payments"
}
```

</details>

<details>
<summary>PUT: /api/acct/payments</summary>

PUT a Json body to send a request containing a single payment record to be upserted and persisted in the database.

- Will validate payment (period and salary validity check).

- Only user of role accountant is authorized to access this endpoint.

Request body:
```json
{
    "employee": "mateuskroeber@acme.com",
    "period": "01-2021",
    "salary": 1234578
}
```

Response body (Http status: 200):
```json
{
    "status": "Updated successfully!"
}
```

Some exception examples:


When request body has any payment in invalid format:
```json
{
    "timestamp": "2023-09-11T01:24:36.016+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid payment format request body.",
    "path": "/api/acct/payments"
}
```

When request body has payment roll containing non existing employee:
```json
{
    "timestamp": "2023-09-11T01:25:18.635+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid employee email!",
    "path": "/api/acct/payments"
}
```
  
</details>

<details>
<summary>GET: /api/admin/user/</summary>

Sends a GET request that responds with all users if current user authenticated is of role administrator.

Response body when user authenticated is administrator (Http status: 200):
```json
[
    {
        "id": 3602,
        "name": "Mauricio",
        "lastname": "Honorato",
        "email": "mauriciohonorato@acme.com",
        "roles": [
            "ROLE_ADMINISTRATOR"
        ]
    },
    {
        "id": 3603,
        "name": "Mateus",
        "lastname": "Kroeber",
        "email": "mateuskroeber@acme.com",
        "roles": [
            "ROLE_ACCOUNTANT",
            "ROLE_USER"
        ]
    },
    {
        "id": 3604,
        "name": "Eduardo",
        "lastname": "Macedo",
        "email": "eduardomacedo@acme.com",
        "roles": [
            "ROLE_USER"
        ]
    }
]
```
  
</details>

<details>
<summary>DELETE: /api/admin/user/{userEmail}</summary>

Sends a DELETE request that deletes user passed in parameter from the database if current user authenticated is of role administrator (and exists).

- Creates logs that user was deleted if sucessful.

Response body when parameter passed in is "fulaninho@acme.com", user authenticated is administrator and "fulaninho@acme.com" is in the database (Http status: 200):
```json
{
    "status": "Deleted successfully!",
    "user": "fulaninho@acme.com"
}
```

Response body when parameter passed in is "fulaninho@acme.com", user authenticated is administrator and "fulaninho@acme.com" is NOT in the database:
```json
{
    "timestamp": "2023-09-11T01:43:51.661+00:00",
    "status": 404,
    "error": "Not Found",
    "message": "User not found!",
    "path": "/api/admin/user/fulaninho@acme.com"
}
```
  
</details>

<details>
<summary>PUT: /api/admin/user/role</summary>

Sends a PUT request to grant/remove role to/from user in the request. Only accessible to user of role administrator. Will also pass through validity checks.

- Creates logs to register changes to user role.

- Roles are separated into two groups, administrative (containing the administrator role) and business (containing the user, auditor and accountant roles). User cannot have roles of different groups, i.e, a user cannot be of administrator and user role at the same time.

- Administrator cannot have role removed.

Request body format to grant role:
```json
{
   "user": "mateuskroeber@acme.com",
   "role": "ACCOUNTANT",
   "operation": "GRANT"
}
```

Response body when user does is not mixing role groups (Http status: 200):
```json
{
    "id": 3603,
    "name": "Mateus",
    "lastname": "Kroeber",
    "email": "mateuskroeber@acme.com",
    "roles": [
        "ROLE_ACCOUNTANT",
        "ROLE_USER"
    ]
}
```

Request body format to remove role:
```json
{
   "user": "mateuskroeber@acme.com",
   "role": "USER",
   "operation": "REMOVE"
}
```

Response body when user is of role and is not his only role (Http status: 200):
```json
{
    "id": 3603,
    "name": "Mateus",
    "lastname": "Kroeber",
    "email": "mateuskroeber@acme.com",
    "roles": [
        "ROLE_ACCOUNTANT"
    ]
}
```

Response body if you tried to add a administrative role to a user that has a business type role and vice versa:
```json
{
    "timestamp": "2023-09-11T01:53:41.571+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "The user cannot combine administrative and business roles!",
    "path": "/api/admin/user/role"
}
```

Response body when trying to remove the administrator role:
```json
{
    "timestamp": "2023-09-11T01:57:49.830+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Can't remove ADMINISTRATOR role!",
    "path": "/api/admin/user/role"
}
```
  
</details>

<details>
<summary>PUT: /api/admin/user/access</summary>

Sends a put request to manually lock/unlock (locking prevents user acess to the api) a specific users account. Only accessible to user of role administrator.

- Creates logs to register event.

- Administrator cannot be locked.

Request body format to lock user:
```json
{
   "user": "eduardomacedo@acme.com",
   "operation": "LOCK" 
}
```

Response body when current authenticated user is of role administrator (Http status: 200):
```json
{
    "status": "User eduardomacedo@acme.com locked!"
}
```

Request body format to unlock user:
```json
{
   "user": "eduardomacedo@acme.com",
   "operation": "UNLOCK" 
}
```

Response body when current authenticated user is of role administrator (Http status: 200):
```json
{
    "status": "User eduardomacedo@acme.com unlocked!"
}
```

Response body when trying to lock user of administrator role:
```json
{
    "timestamp": "2023-09-11T02:03:48.708+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Can't lock the ADMINISTRATOR!",
    "path": "/api/admin/user/access"
}
```
</details>

<details>
<summary>GET: /api/security/events/</summary>

Sends a get request to acess all event logs in the database. Can only be accessed by user of role auditor.

Response body when current user is authenticated and of role auditor (Http status: 200):
```json
[
    {
        "date": "2023-09-10 21:57:48",
        "action": "CREATE_USER",
        "subject": "Anonymous",
        "object": "mauriciohonorato@acme.com",
        "path": "/api/auth/signup"
    },
    {
        "date": "2023-09-10 22:02:38",
        "action": "CREATE_USER",
        "subject": "Anonymous",
        "object": "mateuskroeber@acme.com",
        "path": "/api/auth/signup"
    },
    {
        "date": "2023-09-10 22:10:13",
        "action": "LOGIN_FAILED",
        "subject": "mauriciohonorato@acme.com",
        "object": "/api/auth/changepass",
        "path": "/api/auth/changepass"
    },
    {
        "date": "2023-09-10 22:12:38",
        "action": "CHANGE_PASSWORD",
        "subject": "mauriciohonorato@acme.com",
        "object": "mauriciohonorato@acme.com",
        "path": "/api/auth/changepass"
    },
    {
        "date": "2023-09-10 22:13:01",
        "action": "CHANGE_PASSWORD",
        "subject": "mauriciohonorato@acme.com",
        "object": "mauriciohonorato@acme.com",
        "path": "/api/auth/changepass"
    },
    {
        "date": "2023-09-10 22:13:34",
        "action": "CHANGE_PASSWORD",
        "subject": "mauriciohonorato@acme.com",
        "object": "mauriciohonorato@acme.com",
        "path": "/api/auth/changepass"
    },
    {
        "date": "2023-09-10 22:15:51",
        "action": "GRANT_ROLE",
        "subject": "mauriciohonorato@acme.com",
        "object": "Grant role ACCOUNTANT to mateuskroeber@acme.com",
        "path": "/api/admin/user/role"
    },
    {
        "date": "2023-09-10 22:17:13",
        "action": "CREATE_USER",
        "subject": "Anonymous",
        "object": "eduardomacedo@acme.com",
        "path": "/api/auth/signup"
    },
    {
        "date": "2023-09-10 22:27:18",
        "action": "ACCESS_DENIED",
        "subject": "mauriciohonorato@acme.com",
        "object": "/api/acct/payments",
        "path": "/api/acct/payments"
    },
    {
        "date": "2023-09-10 22:35:16",
        "action": "LOGIN_FAILED",
        "subject": "mauriciohonorato@acme.com",
        "object": "/api/admin/user/",
        "path": "/api/admin/user/"
    },
    {
        "date": "2023-09-10 22:35:41",
        "action": "LOGIN_FAILED",
        "subject": "eduardomacedo@acme.com",
        "object": "/api/admin/user/",
        "path": "/api/admin/user/"
    },
    {
        "date": "2023-09-10 22:35:50",
        "action": "ACCESS_DENIED",
        "subject": "eduardomacedo@acme.com",
        "object": "/api/admin/user/",
        "path": "/api/admin/user/"
    },
    {
        "date": "2023-09-10 22:41:43",
        "action": "CREATE_USER",
        "subject": "Anonymous",
        "object": "fulaninho@acme.com",
        "path": "/api/auth/signup"
    },
    {
        "date": "2023-09-10 22:42:36",
        "action": "DELETE_USER",
        "subject": "mauriciohonorato@acme.com",
        "object": "fulaninho@acme.com",
        "path": "/api/admin/user/fulaninho@acme.com"
    },
    {
        "date": "2023-09-10 22:52:16",
        "action": "GRANT_ROLE",
        "subject": "mauriciohonorato@acme.com",
        "object": "Grant role ACCOUNTANT to mateuskroeber@acme.com",
        "path": "/api/admin/user/role"
    },
    {
        "date": "2023-09-10 22:53:05",
        "action": "REMOVE_ROLE",
        "subject": "mauriciohonorato@acme.com",
        "object": "Remove role USER from mateuskroeber@acme.com",
        "path": "/api/admin/user/role"
    },
    {
        "date": "2023-09-10 23:01:26",
        "action": "LOCK_USER",
        "subject": "mauriciohonorato@acme.com",
        "object": "Lock user eduardomacedo@acme.com",
        "path": "/api/admin/user/access"
    },
    {
        "date": "2023-09-10 23:02:41",
        "action": "UNLOCK_USER",
        "subject": "mauriciohonorato@acme.com",
        "object": "Unlock user eduardomacedo@acme.com",
        "path": "/api/admin/user/access"
    },
    {
        "date": "2023-09-10 23:06:04",
        "action": "GRANT_ROLE",
        "subject": "mauriciohonorato@acme.com",
        "object": "Grant role AUDITOR to eduardomacedo@acme.com",
        "path": "/api/admin/user/role"
    }
]
```
</details>

<details>
<summary>Additional information</summary>

Monitors all endpoints for unauthorized and unauthenticated acess and logs it to the database. If a user fails to authenticate himself 5 times in a row, account is locked automatically and brute force attempt is also logged in for security reasons.

Here's what that looks like in the event logs:
```json
[
    {
        "date": "2023-09-10 23:12:15",
        "action": "LOGIN_FAILED",
        "subject": "eduardomacedo@acme.com",
        "object": "/api/security/events/",
        "path": "/api/security/events/"
    },
    {
        "date": "2023-09-10 23:12:22",
        "action": "LOGIN_FAILED",
        "subject": "eduardomacedo@acme.com",
        "object": "/api/security/events/",
        "path": "/api/security/events/"
    },
    {
        "date": "2023-09-10 23:12:23",
        "action": "LOGIN_FAILED",
        "subject": "eduardomacedo@acme.com",
        "object": "/api/security/events/",
        "path": "/api/security/events/"
    },
    {
        "date": "2023-09-10 23:12:24",
        "action": "LOGIN_FAILED",
        "subject": "eduardomacedo@acme.com",
        "object": "/api/security/events/",
        "path": "/api/security/events/"
    },
    {
        "date": "2023-09-10 23:12:25",
        "action": "LOGIN_FAILED",
        "subject": "eduardomacedo@acme.com",
        "object": "/api/security/events/",
        "path": "/api/security/events/"
    },
    {
        "date": "2023-09-10 23:12:25",
        "action": "BRUTE_FORCE",
        "subject": "eduardomacedo@acme.com",
        "object": "/api/security/events/",
        "path": "/api/security/events/"
    },
    {
        "date": "2023-09-10 23:12:25",
        "action": "LOCK_USER",
        "subject": "eduardomacedo@acme.com",
        "object": "Lock user eduardomacedo@acme.com",
        "path": "/api/security/events/"
    }
]
```
</details>
