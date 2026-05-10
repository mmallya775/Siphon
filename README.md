# Siphon

A CLI for keeping a remote folder in sync with a local one, written in Java.
This script does two functions when run. Initially it will scan the local folder,  
then scan the remote folder and filter out files which have the same name, size and
last modified time. Then it will synchronize the remote folder with the local folder.
In the next stage it will watch the local folder for any changes like creation, deletion, 
modification, etc. and writes to the remote folder immediately.

# Motivation

I have already encountered such a situation where I wanted to maintain a mirror of local folder (Windows) 
with remote folder (Linux) over a persistent connection. That time WinSCP came in handy but also
sparked my curiosity on how such stuff is built. This program is my way of brain storming to create 
a sort of similar functionality.

I have also been learning Java programming and this sort of program sort of helps me to 
learn a programming language the fun way.

## Status

Early. Currently implemented:

- [x] Local folder scanning (size + mtime per file, keyed by relative path)
- [ ] Remote folder scanning over SSH
- [ ] Diff local and remote file lists
- [ ] Implement oneshot sync of files from local to remote
- [ ] Implement uptodate mirroring of local and remote
 
## Requirements

- JDK 25 or later
- Maven 3.9+

[jep512]: https://openjdk.org/jeps/512

## Build

```bash