@echo off

rem Chemin vers temp
set tempsrc = tempsrc

set Salass = SalassFramework

if exist "lib/%Salass%.jar" {
    rd /S /Q "lib/%Salass%"
    echo  %Salass% supprimes
}

mkdir "%tempsrc%" 
rem Cpoie des sources
for /r "src" %%f in (*.java) do copy "%%f" "%tempsrc%"

rem Compilation
javac -cp "lib/*" -d  "%Salass%" "%tempsrc%\*.java"

rem Compression en jar
jar -cf "%Salass%.jar" "%Salass%"

rem suppression du dossier Framework
if exist "%Salass%" {
    rd /S /Q  "%Salass%"
    echo DOssier %Salass% et son contenu sont tous supprimer
}

if exist "%tempsrc%" {
    rd /S /Q "%tempsrc%"
    echo Dossier supprime avec ces contenue
}

rem Deplacement des fichier JAR
move "%Salass%".jar "lib/"
 
pause