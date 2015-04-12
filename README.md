# Browserify for Java
This library implements [Browserify](http://browserify.org/) in Java, so that
a modular Java web application can compose JavaScript modules at runtime, as opposed to the build time.

It uses [Rhino](https://developer.mozilla.org/en-US/docs/Mozilla/Projects/Rhino) internally
to parse JavaScript, find dependencies, and combine them into a single JavaScript file.

## Usage
TBD once the code solidifies.

## TODO
* Write Maven plugin that processes JavaScript at compile-time to discover dependencies and generate an index file
  to capture that. This way, the runtime can mostly avoid parsing JavaScript.
* ParsedSource and Source should merge, and SourceLoader should remember Sources it loaded, so that
  the same source need not be parsed twice in a multi-threaded webapp.