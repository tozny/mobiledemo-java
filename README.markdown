# mobiledemo

Demonstrates an API server that can work with a Tozny-enabled mobile app to
onboard users, and provide access to protected resources.

To configure the demo, create a properties file:

```
src/main/webapp/WEB-INF/tozny.properties
```

and provide values for the `realmKey` and `realmSecret` for your Tozny domain.
Use the file `tozny.properties.example` in the same directory as a reference.

Then run the example using gradle:

```
$ ./gradlew run
```
