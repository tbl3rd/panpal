# panpal

Solve the "palindromic pangram problem"
for Charlie Sawyer's Fall 2014 Java Jive.

Download from https://github.com/tbl3rd/panpal.git

## Usage

    $ lein uberjar
    Created target/uberjar/panpal-0.1.0-SNAPSHOT.jar
    Created target/uberjar/panpal-0.1.0-SNAPSHOT-standalone.jar
    $ java -jar target/uberjar/panpal-0.1.0-SNAPSHOT-standalone.jar
    "Elapsed time: 2949.656 msecs"
    ({:letters 83,
      :words 26,
      :panpal
      ["ma" "regna" "ha" "ya" "fila" "diva" "swob" "zaps" "xis"
      "suq" "us" "raj" "tack" "cat" "jar" "suq" "us"
      "six" "spaz" "bows" "avid" "alif" "ay" "ah" "anger" "am"]}
     {:letters 83,
      :words 26,
      :panpal
      ["ma" "regna" "ha" "ya" "fila" "diva" "swob" "zaps" "xis"
       "suq" "us" "raj" "tuck" "cut" "jar" "suq" "us"
       "six" "spaz" "bows" "avid" "alif" "ay" "ah" "anger" "am"]})
    $

## License

Copyright © 2014 Tom Lyons and so on ...

Distributed under the Eclipse Public License, the same as Clojure.
