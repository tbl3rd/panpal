# panpal

Solve the "palindromic pangram problem"
for Charlie Sawyer's Fall 2014 Java Jive.

Download from https://github.com/tbl3rd/panpal.git

## Usage

    $ ./panpal                              # Just run it.
    ({:letters 83, :words 26,
      :panpal
      ["ma" "regna" "ha" "ya" "fila" "diva" "swob" "zaps" "xis"
      "suq" "us" "raj" "tack" "cat" "jar" "suq" "us"
      "six" "spaz" "bows" "avid" "alif" "ay" "ah" "anger" "am"]}
     {:letters 83, :words 26,
      :panpal
      ["ma" "regna" "ha" "ya" "fila" "diva" "swob" "zaps" "xis"
       "suq" "us" "raj" "tuck" "cut" "jar" "suq" "us"
       "six" "spaz" "bows" "avid" "alif" "ay" "ah" "anger" "am"]})
    $ BOOT_FILE=./panpal boot build         # Build the jar file.
    Compiling 1/1 panpal.core...
    Writing pom.xml and pom.properties...
    Adding uberjar entries...
    Writing panpal-0.1.0.jar...
    $ java -jar target/panpal-0.1.0.jar     # Run the jar.
    ({:letters 83, :words 26,
      :panpal
      ["ma" "regna" "ha" "ya" "fila" "diva" "swob" "zaps" "xis"
      "suq" "us" "raj" "tack" "cat" "jar" "suq" "us"
      "six" "spaz" "bows" "avid" "alif" "ay" "ah" "anger" "am"]}
     {:letters 83, :words 26,
      :panpal
      ["ma" "regna" "ha" "ya" "fila" "diva" "swob" "zaps" "xis"
       "suq" "us" "raj" "tuck" "cut" "jar" "suq" "us"
       "six" "spaz" "bows" "avid" "alif" "ay" "ah" "anger" "am"]})
    $

## License

Copyright © 2014 Tom Lyons and so on ...

Distributed under the Eclipse Public License, the same as Clojure.
