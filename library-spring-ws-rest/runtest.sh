#! /bin/bash -e

# Test Author Controller
./mvnw test -Dtest=no.hvl.dat152.rest.ws.main.test.TestAuthor

# Test Book Controller
./mvnw test -Dtest=no.hvl.dat152.rest.ws.main.test.TestBook

# Test Order Controller
./mvnw test -Dtest=no.hvl.dat152.rest.ws.main.test.TestOrder

# Test User Controller
./mvnw test -Dtest=no.hvl.dat152.rest.ws.main.test.TestUser