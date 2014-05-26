## TL;DR: To Run the solution:

mvn clean package

java -jar target/toy-security-reporter-1.0-SNAPSHOT-jar-with-dependencies.jar


## PROBLEM:

There are json files being written into a directory.  Monitor the directory and process the files.  Output the stats every second.  The example illustrates the specifications.

Example Input:

`{"Type":"Door", "Date":"2014-02-01 10:01:02", "open": true}`

-- then a new file is written, containing an alarm

`{"Type":"Alarm", "Date":"2014-02-01 10:01:01", "name":"fire", "floor":"1", "Room": "101"}`

Example Output (before the alarm is written):

`EventCnt: 1, ImgCnt:0, AlarmCnt:0, avgProcessingTime: 10ms`

After the alarm in the new file is processed, the AlarmCnt would be 1.

## SOLUTION:

### To Run:

<pre>
mvn clean package
java -jar target/toy-security-reporter-1.0-SNAPSHOT-jar-with-dependencies.jar
</pre>

### To generate lots of input files:

Do the following, in a separate terminal, before or after running the above
(assumes a *nix-y system, with ruby >= 1.9.3 on your $PATH):

`./spammer.rb`

This will copy the "template" files (one of each type: not interesting
(the door); an alarm; and an img), into identical-content files of different
names in the `./input` directory, about once every half second, on average.

### Some commentary:

I wanted to provide a self-contained executable that would demonstrate
a working system without having to look at multiple terminals or
files, but also demonstrate some discipline around:

* Design
* Performance
* Scalability

The solution therefore is a single process with a few threads (two
main ones).  You could imagine that the threads were instead separate
processes, communicating over a network.  But, that would make life
more complicated for our purposes here.  The process has three main
components:

* _EventMonitor_, which polls the input directory for new or changed files.
* _StatisticsRepository_, which is the source of record for the statistics.
* _EventCategorizer_, which knows how to parse input files and detect
  whether they are in either of the "watched" categories (alarm and
  img).

### Design notes:

* I elected to use Yammer Metrics as the "data store" for the statistics.
  This is because it does exactly what I needed, is efficient, and
  thread safe.  And, a bonus is, it'd be really easy to change the average
  time metric, to a 5-minute window averge, for example.
* I hmm'd and haw'd about how to do the JSON parsing.  I elected to
  use the streaming API of Jackson because:
  * It is robust with respect to file size: I think it's important to
    protect against very large files.
  * It's fast - probably about as fast asany other parser I could
    write or find.
  * It's thread safe.
  * I like regular expressions, but not THAT much.
* I TDD'd, more or less, the EventCategorizer and StatisticsRepository, so
  they have some unit tests.  The EventMonitor I more or less stole from the
  Internet (reference in the code).
* Performance "testing" using spammer.rb: Sustained processing speed of less
  than 1ms per file on my wimpy old Macbook Air.  Pretty fast! 
  
### Caveats:

* The door.json sample input file is malformed: The value of the Type
  key is not double-quoted.  I've elected to gracefully fail to
  process that sample file, and to say: The input is required to be
  well-formed.  Why?  Because otherwise any parser that would read it
  would have to allow for and interpret ambiguity for some inputs.
* Even touching an existing file will result in an event being
  processed - no effort is made to keep track of files which have
  already been processed.
* There are some areas which I would have liked to put a bit more work
  into:
  * I really don't like how the statistcs collection is woven into the
    file watcher.  Needs for separation of converns.
  * Perhaps using a queue between the file watcher and the event
    processor, which would have allowed me to process all the files in the
    input directory on startup, as well as decouple the implementation
    better.
  * Use some sort of dependency injection and wireup framework - maybe
    not Spring, but something lighter.
* Instead of using a logging framework I use System.out.println
  everywhere.  No apologies.
