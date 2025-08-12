@echo off
setlocal enabledelayedexpansion

rem Chemin vers le repertoire temporaire
set "tempsrc=tempsrc"

rem Nom du Framework
set "Sals=Sals"
set "controller=prom16"

rem Suppression de l'ancien Framework dans lib
if exist "lib\%Sals%.jar" (
    del "lib\%Sals%.jar"
    echo [INFO] Le fichier lib\%Sals%.jar a ete supprime avec succes.
)

rem Creation du repertoire temporaire s'il n'existe pas
if not exist "%tempsrc%" (
    mkdir "%tempsrc%"
    echo [INFO] Le dossier %tempsrc% a ete cree avec succes.
) else (
    echo [INFO] Le dossier %tempsrc% existe dejà.
)

rem Copie des fichiers sources Java dans le dossier temporaire
for /r "src" %%f in (*.java) do (
    copy "%%f" "%tempsrc%\" >nul
)
echo [INFO] Les fichiers Java ont ete copies dans %tempsrc%.

rem Compilation des fichiers Java
pushd "%tempsrc%"
javac -cp "../lib/*" -d . *.java

if %errorlevel% neq 0 (
    echo [ERREUR] echec de la compilation des fichiers Java.
    popd
    pause
    exit /b 1
)

echo [INFO] Compilation reussie.

rem Creation du fichier JAR
jar -cf "../lib/%Sals%.jar" *

if %errorlevel% neq 0 (
    echo [ERREUR] echec lors de la creation du fichier JAR.
    popd
    pause
    exit /b 1
)

echo [INFO] Le fichier JAR %Sals%.jar a ete cree avec succes.

@REM rem Suppression du dossier temporaire
@REM popd
@REM rd /S /Q "%tempsrc%"
@REM echo [INFO] Le dossier %tempsrc% et son contenu ont ete supprimes avec succes.

rem Verification et confirmation de la creation du JAR
if exist "lib\%Sals%.jar" (
    echo [SUCCeS] La librairie %Sals%.jar a ete creee avec succes.
) else (
    echo [ERREUR] La librairie %Sals%.jar n'a pas ete creee.
)

pause
