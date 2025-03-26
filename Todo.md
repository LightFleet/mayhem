1. Room creation:
1) User can create rooms and join other rooms:

```java
createRoom(String name);
joinRoom(String name);
```
whenever someone joins they receive all the messages broadcasted to this room

Client should not be able to create more than 5 rooms in Mayhem Beta.
1 Server should be able to handle at least 10k rooms and 50k clients with 1 MPC / s = 50k MPS;

2. CLI to interact with chat

```bash
Welcome to Mayhem, Alisa
1) Enter room
2) Create room
3) Send Message # (to current room)
4) Exit
```

3. User roles
Admin -> Can do all available "room-scope" functions (ban, invite user, give permissions)
Regular -> Can do standard functions + whatever functions given by admin

4. Need persistence
Custom log file entry
- server.log
- room1.log
Log format: <Timestamp|User|Context|Content>
Ex. 1(server): <03/08/2025/14:34:12:121|Server|Server|Bob created room 1>
Ex. 2(room1): <03/08/2025/14:34:12:121|Bob|Room1|Hi everyone!>

5. Rework Commmand
Working with commands it hard. Need new protocol
```json
{
    "type": "MESSAGE",
    "user": "Bob",
    "context": "Bob room 1",
    "content": "Hi everyone!"
}

```
6. Security
User should be able to register with password. 
His passworded is hashed and saved to memory, hash mapped to cookie. 
Cookie is sent to server with each request.
If client is logged out he can login later and will be sent with a new cookie
___

 <!-- like how my neovim kickstart config gradually loses amout of comments -->

Project ideas:
1) Networking module for chatty
2) some kind of web/pdf crawler (something neat and specific)
3) Something from this list:
    1. ohpas onboarding pdf
    2. ci/cd readiness task automation
    3. Auditing mapper generators
    4. Script to check if you have necessary infra for deployment to PAS Bundle
    5. Tool to autocomplete checks in PR description in SCM
    6. Integration testing framework for OHPAS?
    7. lightweight sqlite database for local development and testing
    8. 
4) Shodan clone?
5) Tower Defence Game
6) Particle system; Shaders?
7) JPEG


























