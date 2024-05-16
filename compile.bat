@echo off

rem Chemin vers repertoire de temp
set tempsrc=tempsrc

rem Nom du FrameWork
set Sals=Sals
set controller=prom16

rem Suppresion des anciens FrameWork dans lib
if exist "lib/%Sals%.jar" (
    rd /S /Q "lib/%Sals%.jar"
    echo Le dossier lib/%Sals%.jar et son contenu ont ete supprimes avec succes.
)

mkdir "%tempsrc%" 
echo Le nouveau dossier %tempsrc% a ete cree avec succes.

rem Copie les sources dans le tempsrc
for /r "src" %%f in (*.java) do copy "%%f" "%tempsrc%"

rem Compilation de tous les fichiers Java du répertoire tempsrc
for %%i in ("%tempsrc%\*.java") do javac -cp "lib/*" -d "." "%%i"jghgj

rem Decompresser en jar
jar -cf "%Sals%.jar" "%controller%"

rem Suppression du dossier controller et tempsrc
if exist "%controller%" (
    rd /S /Q "%controller%"
    echo Le dossier %controller% et son contenu ont ete supprimes avec succes.
)

if exist "%tempsrc%" (
    rd /S /Q "%tempsrc%"
    echo Le dossier %tempsrc% et son contenu ont ete supprimes avec succes.
)

rem Déplace le fichier JAR vers le lib
move "%Sals%.jar" "lib/"



pause
