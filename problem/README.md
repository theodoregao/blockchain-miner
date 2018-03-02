# Coding Challenge

## Problem Description
Blockchain is the new technology of the art. With a view of powering the well known Bitcoin cryptocurrency, new blocks have to be mined into the blockchain. In this challenge, the mining task should be tackled in a distributed manner on smartphones.

Bitcoin mining works in the way that strong mathematical puzzles have to be solved for a proof of work, more specifically calculating a hash of the block's header that is to mine with a certain number of leading zeros (difficulty). If the hash has enough leading zeros, the challenge is solved. Otherwise, the block header’s nuance is altered and a new hash is calculated.

## Challenge Overview
Implement a distributed Bitcoin mining application with following criteria:
* Create a central web service that delegates and aggregates the work to/from the clients. Use NodeJS to implement this logic and deploy it on a server (e.g., Amazon AWS).
* Implement the client hashing logic as an Android app that is responsible for doing the hard calculations in a distributed manner. The client app gets the puzzles to solve from the server and publishes its results to it.
* Follow the REST principles for the communication between client and server. The endpoints could look like this, for instance:
  * ```/work```   - returns a JSON-encoded job that the client shall use to begin mining, e.g.:
    ````
    {
    	jobId: 2
    	clientId: 5,
    	blockHeader: {
    		version: 2,
    		prevBlockhash: 00000000000008a3a41b85b8b29ad444def299fee21793cd8b9e567eab02cd81,
    		merkleRoot: 2b12fcf1b09288fcaff797d71e950e71ae42b91e8bdb2304758dfcffc2b620e3,
    		timestamp: 1305998791,
    		difficultyTarget: 17,
    		Nonce: 2504433986
    	}
    }
    ````
  * ```/submit``` - provides the client with the possibility to submit a successful nonce to the server or when he finished his assigned task.
* Use an appropriate authentication mechanism.
* Realize the client computations in the most performant way you know.
* You can create pseudo-hashing-puzzles and do not have to connect to the real bitcoin blockchain.

## Extensions
Feel free to pick from the following extensions to extend your solution, but they are not required:
* Use Kotlin instead of Java.
* Use TypeScript instead of pure JavaScript.
* Connect your server to the bitcoin blockchain to retrieve “real” block data.

## Guidelines for Submission
Your submission shall contain the following:
* A README.md with a description of the problem and your solution.
* All source code. Package the source files into a zip archive or provide a link to a hosted repository for example on GitHub.
* A link to the server application that is live and functional.

## Evaluation Criteria
This assignment helps us to get insights into your style of software engineering and coding. It is essential to write good quality code that can easily be understood or extended by other developers.
