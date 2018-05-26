**WebEspV3Commander** (work in progress !)

- connect @start
- can reconnect while running (IP choice)
- can preprocess files
- can upload code
- can upload any file via TELNET
- can upload any file via Serial on MCU#2 (LATER)
- can send commands via TELNET (WebSocket @least for reading console output)
- create a TELNET LOCAL WRAPPER
  - that can connect to xts (on a given ip)				OPEN 192.168.4.1
  - that can receive upload request then send to xts		UPLOAD GAME.BAS
  - that can quit										QUIT
  - 