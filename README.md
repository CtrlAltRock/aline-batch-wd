#Aline Batch Processing Microservice

##Description
Reads a formatted Transaction csv and enriches the data, generating dummy data where necessary. Piggybacks off of the Aline Microservices suite and stores relevant data to a similarly formatted database for easier merging and access.

###Requirements

This repository requires you to run my Aline Generator Microservice (found [here](https://github.com/CtrlAltRock/aline-generator-WD)) 

This microservice is responsible for generating relevant data and persisting it to a local database for querying.

###Building and Running

Clone this repository onto your machine, you will need to change some environment variables.

Within "application.properties" you will need to change
    inFile is the path 

