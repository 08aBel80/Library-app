# Library Management System

## Table of Contents
- [Introduction](#introduction)
- [Goal](#goal)
- [Updates](#updates)
  - [Day 1 (2024/06/06)](#day-1-20240606)

## Introduction
This project is a Library Management System built using Kotlin, managed with Gradle. It is designed to manage the borrowing and returning of books in a library.

## Goal
Learn kotlin and gradle, for android development. 

## Updates

### Day 1 (2024/06/06):

started with creating interface for library, added some basic functions.
added book and member data clsses.
added some unit tests for Library.

implemented the functions in Library class.
added more unit tests for Library.

*PLANS FOR NEXT TIME*: create cli for library. where user can interact with the library class.

### Day 2 (2024/06/07):

created cli for library. where user can interact with the library class from console.
I am pretty content with the design of the cli, I think it follows SOLID pattern pretty "solidly" :D 
Tested manually and added some unit tests for cli, wanted to try something different, so I tried to test the cli using plain text file. I wanted to the file to contain input command and expected output. then in test I would read the file and run the command and compare the output with expected output. I worked it out, but it was not as flexible as I wanted it to be. I don't think it is maintainable enough. also need to add more unit tests, didn't have time to do that.

*PLANS FOR NEXT TIME*: I want to add real database to the project, so that the data is persistent. I will also add more unit tests for cli.