# DS-at-UCU-Distributed-Systems

https://docs.google.com/document/d/1D5ZmxwIVRfqyWWR1PvEkNpGwsT9W-OS5V8dsrWjOH-c/

Pre-requisites:
- Gradle
- docker + docker-compose

To run, execute `gradle clean-docker-start`

## Description
### Iteration #1

In docker-compose, we have three services:
- The first one (logs-replication-main) is main node
- The second one (logs-replication-secondary) is the secondary replica;
- For the test simulation, we also have the third one (logs-replication-secondary-stupid). 
  You can modify the environment variable `DELAY_IN_SECONDS` to simulate the delay.

The solution also mechanism for the registration of the secondary nodes.
