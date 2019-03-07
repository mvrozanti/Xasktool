#!/bin/bash
while true; do
    # yes 1 | nc -lp 2718 | while read line; do
    #     if [ "$line" -eq 1 ]; then
    #         break
    #     fi
    # done
    nc -lp 2717 | while read line; do
        echo $line | grep -E "x:.+?,y:.+" > /dev/null
        if [ $? -eq 0 ]; then
            read x y <<< $(echo $line | sd "x:(.+?),y:(.+)" '$1 $2')
            if [[ ! -z $x && ! -z $y ]]; then
                echo $x $y
                xdotool mousemove_relative --sync -- $((2*$x)) $((2*$y))
            fi
        else
            xdotool $line
        fi
        echo 1 >> reverse_pipe
    done
done
