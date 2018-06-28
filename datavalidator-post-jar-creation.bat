@ECHO OFF
ECHO.
ECHO ------------------------------------------------
ECHO Starting post build steps after JAR creation
ECHO ------------------------------------------------
ECHO.
ECHO.
ECHO ************************************************
ECHO Deleting service
ECHO ************************************************
sc delete datavalidator
ECHO.
ECHO.
ECHO ************************************************
ECHO Deleting service setup files
ECHO ************************************************
del /F /Q %cd%\target\datavalidator.exe
del /F /Q %cd%\target\datavalidator.xml
ECHO.
ECHO.
ECHO ************************************************
ECHO Copying resources/* to target/
ECHO ************************************************
cp %cd%\resources\datavalidator.exe %cd%\target\
cp %cd%\resources\datavalidator.xml %cd%\target\
ECHO.
ECHO.
ECHO ************************************************
ECHO Installing service
ECHO ************************************************
cd %cd%\target
echo %cd%
datavalidator.exe install
ECHO.
ECHO.
ECHO ************************************************
ECHO Starting service
ECHO ************************************************
sc start datavalidator
ECHO.
ECHO.
EXIT /B