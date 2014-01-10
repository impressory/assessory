Assessory
=========

Open source assessment software using Angular.js, Play, and Handy. Initially supporting group critiques.

When I wrote it, it had few unusual requirements. For instance students were already in groups, and had GitHub accounts, but did not have accounts on Assessory. This led to the somewhat unusual "Pre-enrol" and "Group pre-enrol" features.

The original application was written in a week for DECO2800 at The University of Queensland.

So if you find it doesn't do what you need, [raise an issue](http://github.com/impressory/assessory/issues) and it probably won't take very long to make it do what you want. 


### Using the distribution zip

1. Download the [distribution zip file](https://docs.google.com/uc?id=0Byc3ursv9OXJdUVjdC14Yk5zMWs&export=download) 
2. Unpack the zip file
3. Set the environment variables for your database config and social logins (see below)
4. Run the `bin/assessory` script from the zip file. 
   You might need to give it execute permissions using `chmod u+x bin/assessory`, and should run it using `nohup` so that the process won't terminate when you close the shell.
5. The server will now be running, listening on port 9000

### Compiling from source

Setting up Assessory requires

* MongoDB (either locally or remote) 
* [sbt](http://scala-sbt.org), the Scala Build Tool. version 0.13 or higher.
* A Java virtual machine

1. Clone the repository
2. `sbt dist`. This builds the "distribution package" -- a zipfile of everything you need. 
   sbt will automatically locate and download the required dependencies.
3. Unpack the zip file
4. Set the environment variables for your database config and social logins (see below)
5. Run the `bin/assessory` script from the zip file. 
   You might need to give it execute permissions using `chmod u+x bin/assessory`, and should run it using `nohup` so that the process won't terminate when you close the shell.
6. The server will now be running, listening on port 9000


### HTTPS

To make Assessory listen using HTTPS, you'll need to pass a few more parameters to the start script, as described [here](http://www.playframework.com/documentation/2.2.x/ConfiguringHttps)

   
### Databaase environment variables

The following environment variables configure the MongoDB connection:

    $ASSESSORY_MONGO_URL
    $ASSESSORY_MONGO_DBNAME
    $ASSESSORY_MONGO_DBUSER
    $ASSESSORY_MONGO_DBPWD
    
These can also be set by passing them as arguments to the start script

    -Dmongo.connection=
    -Dmongo.dbname= 
    -Dmongo.dbuser= 
    -Dmongo.dbpwd= 
    
### Social media logins

Students can log in using GitHub or Twitter accounts. 

For this to work, you'll need to set an OAuth client key and secret that is issued to you by GitHub or Twitter.

GitHub:

1. Register an application on [GitHub](https://github.com/settings/applications/new) 

   The authorisation callback URL is `{your server URL}` + `/oauth/github/callback`

2. Set the Client ID and Client Secret before starting Assessory. This can be done by setting these environment variables
```
   ${ASSESSORY_AUTH_GITHUB_CKEY}
   ${ASSESSORY_AUTH_GITHUB_CSECRET}
```
   or by passing them as arguments to the start script
```
   -Dauth.github.ckey=
   -Dauth.github.csecret=
```

Twitter:

1. Register an application on [Twitter](https://dev.twitter.com/apps) 

   The authorisation callback URL is `{your server URL}` + `/oauth/twitter/callback`

2. Set the Client ID and Client Secret before starting Assessory. This can be done by setting these environment variables
```
    ${ASSESSORY_AUTH_TWITTER_CKEY}
    ${ASSESSORY_AUTH_TWITTER_CSECRET}
```
   or by passing them as arguments to the start script
```
   -Dauth.twitter.ckey=
   -Dauth.twitter.csecret=
```
