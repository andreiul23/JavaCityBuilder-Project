@echo off
setlocal enabledelayedexpansion

REM Navigate to project root
cd /d "%~dp0"

REM Display Java version for debugging
echo Current Java version:
java -version

REM Ensure output directory exists
if not exist target\classes mkdir target\classes

REM Copy resources
xcopy /E /Y src\main\resources\* target\classes\ >nul 2>&1

REM Compile all Java files (standard layout)
echo.
echo Compiling Java files...
javac -d target\classes src\main\java\main\*.java src\main\java\boardPieces\*.java

if %errorlevel% neq 0 (
    echo.
    echo Compilation failed!
    pause
    exit /b 1
)

REM Run the application
echo.
echo Starting City Builder...
java -cp target\classes main.Runner

if %errorlevel% neq 0 (
    echo.
    echo Application failed to run!
    pause
    exit /b 1
)
