## Configure Keycloak

### Create a new realm

1. Log in to Keycloak Admin Console http://localhost:9081/ .
2. Click on "Add Realm".
3. Enter a name for the realm `petclinic`.

### Create a new client

1. In the "petclinic" realm, go to "Clients".
2. Click on "Create".
3. Enter the following details:
   - Client ID: `sb`
   - Client Protocol: `openid-connect`
   - Root URL: `http://localhost:8080/`
   - Valid Redirect URIs: `http://localhost:8080/*`
4. Set up client secret in application.properties

### Create a new user

1. In the "petclinic" realm, go to "Users".
2. Click on "Add User".
3. Enter the following details:
   - Username: `user`
   - First Name: `User`
   - Last Name: `User`
   - Email: `user@example.com`
   - Email Verified: `on`
4. Go to the "Credentials" tab.
5. Set a password for the user (e.g., `password`). Do not forget to toggle "Temporary" off.
