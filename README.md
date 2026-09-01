# JWT Auth Server

![Java](https://img.shields.io/badge/Java-8-ED8B00?logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.1.2-6DB33F?logo=springboot&logoColor=white)

A standalone Spring Boot microservice that issues signed JSON Web Tokens from a username/password login endpoint. It's a from-scratch look at how token issuance works under the hood — using `io.jsonwebtoken` (jjwt) directly to build and sign HS256 tokens with a 30-minute expiry — rather than relying on a framework's auth-starter or an API gateway policy to do it.

## How this differs from my other API/security repos

This is the only repo in the cluster that isn't MuleSoft at all — it's a plain Java/Spring Boot service, and the only one implementing token-based (JWT) authentication rather than Basic Auth or OAuth2. The `mule-custom-*` repos are API Gateway policies/fallback endpoints, and the `apidev-*` repos are Mule integrations doing inbound Basic Auth enforcement or outbound Basic/OAuth2 calls — this repo is the one that actually mints the credential (a JWT) that a downstream service could check.

## Tech Stack

- Java 8, Spring Boot 2.1.2 (`spring-boot-starter-web`, `spring-boot-starter-actuator`)
- `io.jsonwebtoken` (jjwt) 0.10.5 — API, impl, and Jackson serialization modules
- Maven / Maven Wrapper (`mvnw`)
- JUnit 4 + Spring Boot Test (context-load smoke test only)

## How it works

- `App.java` exposes a single endpoint, `POST /authorization_service`, taking `userName` and `password` as request headers.
- If both are present, it delegates to `JwTokenHelper` (a singleton) to build a JWT: issuer = username, subject = password, a custom claim of `username: password`, signed with an HS256 key generated once at process startup, expiring 30 minutes out.
- The generated token is returned as plain text in the response body.
- `JwTokenHelper` also has `claimKey`/`verifyJWTToken` methods for parsing/validating a token, but no endpoint in `App.java` currently calls them — there's no `/validate` route, so token verification isn't exercised end-to-end yet.

This is intentionally a minimal proof of concept for token issuance mechanics, not a full auth server — there's no user store, no password hashing, and the signing key lives only in memory for the life of the process (a restart invalidates every previously issued token).

## Getting Started

Requires Java 8+ (the Maven Wrapper handles the rest).

```bash
# Run
./mvnw spring-boot:run

# Or build a jar and run it
./mvnw clean package
java -jar target/jwt-auth-server-0.0.1-SNAPSHOT.jar
```

Then issue a token:

```bash
curl -X POST http://localhost:8080/authorization_service \
  -H "userName: alice" \
  -H "password: secret"
```

## Project Structure

```
├── src/main/java/org/tech/poc/app/App.java             # Spring Boot app + login endpoint
├── src/main/java/org/tech/poc/app/JwTokenHelper.java     # JWT generation/parsing helper
├── src/main/resources/application.properties             # Spring Boot config
├── src/test/java/.../JwtAuthServerApplicationTests.java   # context-load smoke test
├── mvnw / mvnw.cmd                                        # Maven Wrapper
└── pom.xml                                                # Maven build + dependencies
```
