@echo off
setlocal enabledelayedexpansion

rem === Configuration ===
set "nameProject=SPRINT"
set "temp=temp"
set "tempsrc=tempsrc"

rem === Suppression des anciens repertoires ===
for %%D in ("%temp%", "%tempsrc%") do (
    if exist "%%D" (
        rd /S /Q "%%D"
        echo [INFO] Le dossier %%D et son contenu ont ete supprimes avec succes.
    )
)

rem === Creation des repertoires necessaires ===
for %%D in ("%temp%", "%tempsrc%") do (
    mkdir "%%D"
    echo [INFO] Le dossier %%D a ete cree avec succes.
)

rem === Definition des chemins ===
set "librairie=lib"
set "xml=web.xml"
set "src=src"
set "view=views"

set "deslibrairie=%temp%\WEB-INF\lib\"
set "desxml=%temp%\WEB-INF\"
set "desview=%temp%\view\"
set "classesDir=%temp%\WEB-INF\classes\"

rem === Copie des fichiers et dossiers ===
xcopy "%librairie%" "%deslibrairie%" /E /I /Y >nul
echo [INFO] Copie du dossier %librairie% effectuee avec succes.

xcopy "%view%" "%desview%" /E /I /Y >nul
echo [INFO] Copie du dossier %view% effectuee avec succes.

copy "%xml%" "%desxml%" >nul
echo [INFO] Copie du fichier %xml% effectuee avec succes.

rem === Copie et compilation des fichiers Java ===
for /r "%src%" %%f in (*.java) do (
    copy "%%f" "%tempsrc%" >nul
)

echo [INFO] Copie des fichiers Java dans %tempsrc% terminee.

javac -cp "%deslibrairie%*" -d "%classesDir%" "%tempsrc%\*.java"

if %errorlevel% neq 0 (
    echo [ERREUR] echec de la compilation des fichiers Java.
    pause
    exit /b 1
)

echo [INFO] Compilation reussie. Les fichiers .class sont stockes dans %classesDir%.

rem === Creation du fichier WAR ===
set "projet=%nameProject%.war"
jar -cvf "%projet%" -C "%temp%" . >nul

if %errorlevel% neq 0 (
    echo [ERREUR] echec lors de la creation du fichier WAR.
    pause
    exit /b 1
)

echo [INFO] Le fichier WAR a ete cree : %projet%

rem === Deploiement dans Tomcat ===
set "deployPath=C:\Program Files\Apache Software Foundation\Tomcat 10.1\webapps"
move "%projet%" "%deployPath%" >nul

if %errorlevel% neq 0 (
    echo [ERREUR] echec du deploiement du fichier WAR.
    pause
    exit /b 1
)

echo [SUCCeS] Deploiement effectue avec succes dans %deployPath%.

pause
