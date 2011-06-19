@ECHO OFF

SET PROGDIR=%~d0%~p0

if not exist "%PROGDIR%yajhfc.jar" (
	echo "%PROGDIR%yajhfc.jar not found!"
	exit /B 1 
)

if not exist "%PROGDIR%yajhfc-console.jar" (
	echo "%PROGDIR%yajhfc-console.jar not found!"
	exit /B 1 
)

java -jar "%PROGDIR%yajhfc-console.jar" %*

