# How to find CPU hogs in EBX

## Determine the Process Id (PID) of EBX

    ps w -C java

Show the treads for that PID. There may be more than one with high CPU usage.

    ps -Lp <PID> | sort -k4

The threads using the most CPU will be at the bottom of the list.

## Create a thread dump

    sudo kill -3 <PID>

This creates a `javacore` text file in the tomcat home directory `/usr/share/tomcat` which I move for analysis so they don't pile up.

    mv /usr/share/tomcat/javacore.*.txt /usr/share/ebx/troubleshoot-cpu

Open the file and search for "THREADS" to jump to the relevant section. From there look at the "CPU usage total:" for each thread to find the ones causing all the trouble.

# Other useful commands

Display the current running processes and other system information. Press `q` to quit.

    top

Show amount of free disk space.

    df -h .

Show amount of free memory.

    free -h

