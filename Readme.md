# OAuth2 Demo

It use OAuth2 client_credentials grant type to get JWT token for authorization.

## File Structure

* oauth2-util: utilities used by auth-server, resource-server and client. It contains KeyFactory that loads two ClientKeyStore and ResourceKeyStore and is used by other projects to create and verify JWT tokens.

* auth-server: Authorization server

* resource-server: Resource server

* client: client application for testing. It has two ways to get a JWT token to use in a request to Authorization server to get access token

    1. local: use ClientJwtUtil to get JWT token locally.

    2. remote: use REST call which also uses ClientJwtUtil to get JWT token.

* echo-svr: Http server simply returns caller's headers and content in JSON format

* bridge-proxy: utility package. See its Readme.md for more information.

## Key Stores

Two key stores are required.

1. ClientKeyStore: used by client to create JWT to request access token from Authorization server, and Authorization server to verify the client request. It is used by _auth-server_ and _client_ to create and verify client request JWT token.

>keytool -genkeypair -alias oauth2test -keyalg RSA -keystore ClientKeyStore.jks -storepass changeit -keypass changeit

2. ResourceKeyStore: used by Authorization server to create access token, and Resource server to authorize client request. It is used by _auth-server_ to create access token and _client_ indirectly to request service from _resource-server_

>keytool -genkeypair -alias oauth2test -keyalg RSA -keystore ResourceKeyStore.jks -storepass changeit -keypass changeit

Note: alias values in two key stores must be the same. Because, in demo configuration settings, client uses the alias as iss and sub in request JWT. And then, Authorization server, uses it as client_id in returned access token.

## Configuration and Spring Properties/Environments

1. ClientKeyStore:

    * oauth2.certificatePath: path to ClientKeyStore.
    * oauth2.certificateAlias: alias for key in ClientKeyStore.
    * oauth2.certificatePassword: password for ClientKeyStore and alias key

    All are used by util.KeyFactory class as Java system properties. Must be specified in eclipse launches

    * AuthorizationServer
    * JwtClient
    * ClientJwtGenerateUrl (Optional, if it is used)

2. ResourceKeyStore:

    * oauth2.keystorePath: path to ResourceKeyStore.
    * oauth2.keystoreAlias: alias for key in ResourceKeyStore.
    * oauth2.keystorePassword: password for ResourceKeyStore and alias key

    All are used by util.KeyFactory class as Java system properties. Must be specified in eclipse launches

    * AuthorizationServer

3. resource.id: It is mapped to Spring package property security.oauth2.resource.id. It is used in two places

    * defined in access token When AuthorizationServer creates it

    * defined in Resource Server configuration and used to match the resource.id in access token send by client

    Its value can be any string. Must be specified in

    * Authorization server application.properties

    * Resource server application.properties

4. oauth.client.jwt.issuer: It is the _iss_ specified in JWT token when client requests access token. It must be specified in

    * client's application.properties

    * Match the client value specified in Spring security inMemory configuration. See AuthorizationServerConfiguration.configure() method in AuthorizationServer.

    It must also match oauth2.keystoreAlias and oauth2.certificateAlias

5. oauth.client.jwt.scope: It is the value scope used in both client request token and Authorization server returned access token. It is a string but cannot contain space. Its usage needs more understand. It is used by ClientJwtGenerateUrl in client.jwt package. It must be specified in

    * client's application.properties

    * Match the scopes value specified in Spring security inMemory configuration. See AuthorizationServerConfiguration.configure() method in AuthorizationServer.

6. oauth.web.gateway: url for remote client request JWT generator. It is needed only if client gets its request JWT token from remote. It must be specified in

    * client's application.properties

7. Spring security role specified as authorities in inMemory configuration. - not used now and should modify the application to use it.

## Port

* 8901: Authorization server listening port
* 8902: Resource server listening port
* 8903: echo srv listening port
* 8904: ClientJwtGenerateUrl listening port

## Run Application

1. start Authorization server
2. start Resource server
3. start JwtClient

To use ClientJwtGenerateUrl to create client request JWT

1. start Authorization server
2. start Resource server
3. start ClientJwtGenerateUrl
4. start JwtClient (remote JWT)