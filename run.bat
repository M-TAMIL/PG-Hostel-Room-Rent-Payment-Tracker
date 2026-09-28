@echo off
setlocal
echo ========================================================
echo   RentEasy - PG and Hostel Room Rent Tracker
echo ========================================================
echo.

set MAVEN_CMD=mvn
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    if exist "%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.16\0daed3be3ebd1c706f0e69e8b07c6b73f5cc4ea3dfce72a8d0ec2e849ca2ddb0\bin\mvn.cmd" (
        set MAVEN_CMD="%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.16\0daed3be3ebd1c706f0e69e8b07c6b73f5cc4ea3dfce72a8d0ec2e849ca2ddb0\bin\mvn.cmd"
    ) else (
        echo [ERROR] Maven not found in PATH or .m2 cache. Please install Maven or add to PATH.
        pause
        exit /b 1
    )
)

echo Starting RentEasy Spring Boot Application...
call %MAVEN_CMD% spring-boot:run
pause
