# JWT TOKEN

## How to use it

First of all you need to decide if the project using this dependency is PROVIDER or RESOURCE.

### PROVIDER

Project responsible for providing JWT tokens to authenticate users, such as an authentication API.

A PROVIDER can also work as a RESOURCE, but not the other way around.

#### Required configuration

* **application.authentication.jwt.service-type**: **_PROVIDER_**
* **application.authentication.jwt.private-key**: eg.: **_classpath:private_key.pem_** - private key location
* **application.authentication.jwt.public-key**: eg.: **_classpath:public_key.pem_** - public key location
* **application.authentication.jwt.access-token-expiration**: eg.: **_3600_** expiration in seconds
* **application.authentication.jwt.refresh-token-expiration**: eg.: **_86400_** expiration in seconds
* **application.authentication.jwt.issuer**: eg.: **_https://site.biz_** issuer of the JWT
* **application.authentication.jwt.audience**: eg.: **_application-name_** audience of the JWT

### RESOURCE

Project that will validate access, an API in the project for example, that do not authenticate users (that would be a resource), but needs authenticated user to allow access to its endpoints

* **application.authentication.jwt.service-type**: **_RESOURCE_**
* **application.authentication.jwt.public-key**: eg.: **_classpath:public_key.pem_** - public key location
* **application.authentication.jwt.issuer**: eg.: **_https://site.biz_** issuer of the JWT
* **application.authentication.jwt.audience**: eg.: **_application-name_** audience of the JWT

## Generating Tokens

You can call JwtTokenProvider to generate tokens, wither access token, refresh tokens or both

```java
TokenData accessToken = jwtTokenProvider.generateAccessToken(user);
TokenData refreshToken = jwtTokenProvider.generateRefreshToken(user);
```

TokenData object will return:

* **token**: the access token itself
* **issuedAt**: the date time of the issued token

To enable your User class/object to be sent to JwtTokenProvider it needs to implement the interface **JwtUserData**

* **getUserAuthoritiesName**: The list of authorities/roles names 
* **getUserEmail**: the user email to validate the authentication
* **getUserTenancyId**: If you use multitenancy the tenancy id can be added to the token
* **getUserTenancyName**: If you use multitenancy the tenancy name can be added to the token

## Filter to validate access

You need to create a filter that will be used for each request (or the way you like) in order to validate access.

This filter can make usage of this api JwtTokenAuthenticationCheck component that returns the Spring Security object **UsernamePasswordAuthenticationToken**

With **UsernamePasswordAuthenticationToken** you can do whereaver you like

```java
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenAuthenticationCheck jwtTokenAuthenticationCheck;

    public JwtTokenAuthenticationFilter(JwtTokenAuthenticationCheck jwtTokenAuthenticationCheck) {
        this.jwtTokenAuthenticationCheck = jwtTokenAuthenticationCheck;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authentication = jwtTokenAuthenticationCheck.getAuthentication(request);
        if (authentication == null) {
            filterChain.doFilter(request, response);
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
```

Also, apply this filter into your Security Configuration

```java
httpSecurity.addFilterBefore(new JwtTokenAuthenticationFilter(jwtTokenAuthenticationCheck), UsernamePasswordAuthenticationFilter.class)
```

