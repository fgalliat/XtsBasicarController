5 ' @ this time esp require a blank line
10 I = 1
20 CLS
30 ? "   -= ESP MENU =-"
40 ' FOR L = 1 TO 3 : LOCATE 0,L+2 : ? " " : NEXT L
50 LOCATE 0,I+1 : ? ">";
100 LOCATE 2,2 : ? "Raycast";
110 LOCATE 2,3 : ? "New Menu";
120 LOCATE 2,4 : ? "Monkey";
125 LOCATE 2,5 : ? "Ext Menu";
128 LOCATE 2,6 : ? "Wifi";
130 LOCATE 2,7 : ? "Exit";
200 A$=INKEY$ : CH = 0
210 IF A$="r" OR BTN(6) = 1 THEN I = I - 1 : CH = 1
220 IF A$="c" OR BTN(7) = 1 THEN I = I + 1 : CH = 1
230 IF A$=" " OR BTN(1) = 1 THEN CH = 1: DELAY 300 : GOTO 1000
300 IF I < 1 THEN I = 6
310 IF I > 6 THEN I = 1
600 IF CH = 1 THEN DELAY 200 : GOTO 20
610 DELAY 10 : GOTO 200
1000 IF BTN(1) = 1 THEN GOTO 1000
1001 IF I = 6 THEN STOP
1003 IF I = 5 THEN CHAIN "WIFMENU" : ' CHAIN "LABY"
1005 IF I = 4 THEN CHAIN "EXTMENU"
1010 IF I = 3 THEN DRAWBPP "METI" : PLAYT5K "MONKEY" : IF BTN(1) = 1 THEN GOTO 1010
1020 IF I = 2 THEN CHAIN "NEWMENU"
1030 IF I = 1 THEN CHAIN "RAYC4"
1040 ' IF I = 1 THEN LOAD "RAYC" : RUN
2000 GOTO 10
