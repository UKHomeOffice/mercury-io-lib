Scala library for IO functionality
==================================
General Scala IO functionality such as reading resources, JSON schema validation and web services.

Project built with the following (main) technologies:

- Scala

- SBT

- Json4s

Introduction
------------
TODO

Build and Deploy
----------------
The project is built with SBT. On a Mac (sorry everyone else) do:
```
brew install sbt
```

It is also a good idea to install Typesafe Activator (which sits on top of SBT) for when you need to create new projects - it also has some SBT extras, so running an application with Activator instead of SBT can be useful. On Mac do:
```
brew install typesafe-activator
```

To compile:
```
sbt compile
```

or
```
activator compile
```

To run the specs:
```
sbt test
```

To run integration specs:
```
sbt it:test
```

SBT - Revolver (keep things going while developing/testing)
-----------------------------------------------------------
sbt-revolver is a plugin for SBT enabling a super-fast development turnaround for your Scala applications:

See https://github.com/spray/sbt-revolver

For development, you can use ~re-start to go into "triggered restart" mode.
Your application starts up and SBT watches for changes in your source (or resource) files.
If a change is detected SBT recompiles the required classes and sbt-revolver automatically restarts your application. 
When you press &lt;ENTER&gt; SBT leaves "triggered restart" and returns to the normal prompt keeping your application running.

Example Usage - JSON
--------------------
- Validate JSON against a JSON schema:
```scala
  val json: JValue = getYourJson()
  val schema: JValue = getYourSchema()
  
  val Good(result) = JsonSchema(schema).validate(json) // Assuming successful validation
```

- Transform JSON from one structure to another:
```scala
  val yourJsonTransformer = new JsonTransformer {
    def transform(json: JValue): JValue Or JsonError = {
      val JsonTransformation(oldJson, newJson) = (
        map("name" -> "superName") ~
        mapArray("fee" -> "payment.feeInPence", field => JInt(BigInt(field.extract[String])))
      )(json)
      
      Good(newJson)
    }
  }
  
  val flatJson = parse("""
  {
    "name": "Batman",
    "fee_1": "12",
    "fee_2": "15",
    "fee_3": 18
  }""")

  val json = parse("""
  {
    "superName": "Batman",
    "payment": [
      { "feeInPence": 12 },
      { "feeInPence": 15 },
      { "feeInPence": 18 }
    ]
  }""")

  // Assuming successful transformation
  transform(flatJson) mustEqual Good(json) 
```

Note - if required (though not advised) the EmptyJsonSchema can be used to all JSON to be validated successfully.

JSON Schema Validation
----------------------
There are several online JSON schema validation tools such as [JSON Schema Validator](http://www.jsonschemavalidator.net/)

Alternatively, a JSON schema can be validated from the Scala REPL by doing the following:

```
sbt

console

import org.json4s.JValue
import uk.gov.homeoffice.io.{Classpath, Resource}
import uk.gov.homeoffice.json._

Resource(Classpath("/schema-test.json")).to[JValue] map { JsonSchema(_) }
```

If you've given a valid file path and the schema is valid, the result will be something like:

```
res0: scala.util.Try[uk.gov.homeoffice.json.JsonSchema] = Success(uk.gov.homeoffice.json.JsonSchema@2728f35d)
```

Example Usage - Web Services
----------------------------
You have some service that can publish a file to some web service:
```
class ExamplePublisher(ws: WebService) {
  val publish: File => Future[WSResponse] = { file =>
    ws.endpoint("/my-file-endpoint").post(file)
  }
}
```

Let's test this by faking the endpoint i.e. an integration test but without requiring a separating running service with the required endpoint:
```
class ExamplePublisherSpec(implicit env: ExecutionEnv) extends Specification with WebServiceSpecification {
  "File" should {
    "be published to endpoint" in {
      withRoutes {
        case POST(p"/my-file-endpoint") => Action(parse.multipartFormData) { request =>
          request.body.files.foreach { filePart =>
            println("Thank you for file " + filePart.filename)
            filePart.ref.moveTo(new File(s"./read-${filePart.filename}"))
          }

          Ok("blah")
        }
      } { webService =>
        val examplePublisher = new ExamplePublisher(webService)

        examplePublisher.publish(new File("my-file")) must beLike[WSResponse] {
          case r: WSResponse =>
            r.status mustEqual OK
            r.body mustEqual "blah"
        }.await
      }
    }
  }
}
```

Releasing
---------
Version control of this library can be achieved through the SBT Release plugin e.g.
```
sbt release
```

where the following default value will be chosen:
- Continue with snapshots dependencies: no
- Release Version: current version without the qualifier (eg. 1.2.0-SNAPSHOT -> 1.2.0)
- Next Version: increase the minor version segment of the current version and set the qualifier to '-SNAPSHOT' (eg. 1.2.1-SNAPSHOT -> 1.3.0-SNAPSHOT)
- VCS tag: abort if the tag already exists
- VCS push:
    - Abort if no remote tracking branch is set up.
    - Abort if remote tracking branch cannot be checked (eg. via git fetch).
    - Abort if the remote tracking branch has unmerged commits.
    - Set release version and next version as command arguments

You can set the release version using the argument release-version and next version with next-version.

Example (within sbt):
```
release release-version 1.0.99 next-version 1.2.0-SNAPSHOT
```

or
```
release with-defaults
```