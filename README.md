# Kloc The Game

[![Build Status](https://api.travis-ci.org/note/kloc-the-game.svg)](https://travis-ci.org/note/kloc-the-game)

# About project

This project was intended to be implementation of the "kloc" - variation of chess. For now just chess logic has been implemented.



# How to run

## Prerequisites

SBT installed. More in [installation guide] (http://www.scala-sbt.org/0.13/docs/Setup.html).

## Running project

To run:

```bash
sbt web/run
```

This command starts server listening at `localhost:9000`.

## Running tests

To run all tests:

```bash
sbt test
```

To run only chess logic tests:

```
sbt rules/test
```

To run web (both frontend and backend) tests:

```
sbt web/test
```

Mind that javascript tests will run on rhino (JVM's javascript implementation). If you want to run tests inside browser then:

```
sbt web/jasmineGenRunner
```

This command's result is a path to file. If you enter returned file in browser you'll be able to run tests in browser.

## Tips and tricks

You can run all sbt commands in sbt console. So instead of `sbt web/test` you can open sbt console with `sbt` and then
run abitrary sbt commands (`web/test` in that case). That way sbt is initialized just once.
