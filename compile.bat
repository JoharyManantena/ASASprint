@echo off
setlocal enabledelayedexpansion

rem Chemin vers le repertoire temporaire
set tempsrc=tempsrc

rem Nom du Framework
set Sals=Sals
set controller=prom16

rem Suppression des anciens Framework dans lib
if exist "lib\%Sals%.jar" (
    del "lib\%Sals%.jar"
    echo Le fichier lib\%Sals%.jar a ete supprime avec succes.
)

rem Creation du repertoire temporaire (ignore si deja existant)
if not exist "%tempsrc%" (
    mkdir "%tempsrc%"
    echo Le nouveau dossier %tempsrc% a ete cree avec succes.
) else (
    echo Le dossier %tempsrc% existe deja.
)

rem Copie des fichiers sources Java dans le dossier temporaire
for /r "src" %%f in (*.java) do (
    copy "%%f" "%tempsrc%\" >nul
)
echo Les fichiers Java ont ete copies dans %tempsrc%.

rem Compilation des fichiers Java
cd %tempsrc%
set errorOccurred=false

rem Compilation de tous les fichiers Java en conservant les packages
javac -cp "../lib/*" -d . *.java

if !errorlevel! neq 0 (
    echo Erreur lors de la compilation des fichiers Java.
    set errorOccurred=true
)

if "!errorOccurred!" == "true" (
    echo Des erreurs de compilation ont eu lieu. Veuillez les corriger avant de créer le JAR.
    pause
    exit /b 1
)

echo Compilation reussie.

rem Creation du fichier JAR
jar -cf "../lib/%Sals%.jar" *
if !errorlevel! neq 0 (
    echo Erreur lors de la creation du fichier JAR.
    pause
    exit /b 1
)
echo Le fichier JAR %Sals%.jar a ete cree avec succes.

rem Suppression du dossier temporaire
cd ..
if exist "%tempsrc%" (
    rd /S /Q "%tempsrc%"
    echo Le dossier %tempsrc% et son contenu ont ete supprimes avec succes.
)

rem Verification et annonce de la creation du fichier JAR
if exist "lib\%Sals%.jar" (
    echo La librairie %Sals%.jar a ete cree avec succes.
) else (
    echo Erreur : La librairie %Sals%.jar n'a pas ete cree.
)

pause
