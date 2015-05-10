arallon
============================

Strongly Typed Time for Scala.

See [http://blog.themillhousegroup.com/2015/04/strongly-typed-time-part-1-rationale.html](http://blog.themillhousegroup.com/2015/04/strongly-typed-time-part-1-rationale.html)


### Installation

Bring in the library by adding the following to your ```build.sbt```. 

  - The release repository: 

```
   resolvers ++= Seq(
     "Millhouse Bintray"  at "http://dl.bintray.com/themillhousegroup/maven"
   )
```
  - The dependency itself: 

```
   libraryDependencies ++= Seq(
     "com.themillhousegroup" %% "arallon" % "0.1.10"
   )

```

### Usage

Once you have __arallon__ added to your project, you can start using it like this:

```
import com.themillhousegroup.arallon._
import com.themillhousegroup.arallon.zones._

val nowInParis = TimeInZone[Paris]
// TimeInZone[Paris] UTC: '2015-05-10T03:04:15.876Z' Local: '2015-05-10T05:04:15.876+02:00'

val threePMParisConferenceCall = nowInParis.transform(_.withTime(15,0,0,0))
// TimeInZone[Paris] UTC: '2015-05-10T13:00:00.000Z' Local: '2015-05-10T15:00:00.000+02:00

val wakeUpCall = fPMParisConferenceCall.map[Sydney]
// TimeInZone[Sydney] UTC: '2015-05-10T13:00:00.000Z' Local: '2015-05-10T23:00:00.000+10:00'

```
Notice how performing the `map` to Sydney-time didn't change the instant being referred to - which is what you want if you're trying to join a conference call starting at that instant.



If you're looking for something client-side to generate a nice IANA string (like `Europe/Paris`) take a look at
[jstimezonedetect on GitHub](https://bitbucket.org/pellepim/jstimezonedetect) - pass that to your Scala server-side and you can be strongly-typed
from then on.


### Still To-Do

Lots.

### Credits
 - The fantastic [Joda-Time](http://www.joda.org/joda-time/) does all the heavy lifting
 
 
### Arallon?
The project is named after a fictional island in [Episode 6 of the first season of HBO's _Silicon Valley_](http://www.hbo.com/silicon-valley/episodes/1/06-third-party-insourcing/synopsis.html#/). The island was being built directly on the International Date Line - half in one timezone (and hence day), half in the other.

The name appealed for a couple of reasons - firstly, dealing with time on such an island would be hugely annoying and error-prone. Secondly, the construction of the island itself was fully automated using robotics and computers - no humans were involved.

The aim of _this_ project is to approach that level of automation, where the _compiler_ ensures that errors don't occur when working with timezones.

