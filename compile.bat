@echo off

rem Chemin vers repertoire de temp
set tempsrc=tempsrc

rem Nom du FrameWork
set Sals=Sals
set controller=prom16

rem Suppression des anciens FrameWork dans lib
if exist "lib/%Sals%.jar" (
    del "lib/%Sals%.jar"
    echo Le fichier lib/%Sals%.jar a ete supprime avec succes.
)

mkdir "%tempsrc%" 
echo Le nouveau dossier %tempsrc% a ete cree avec succes.

rem Copie les sources dans le tempsrc
for /r "src" %%f in (*.java) do copy "%%f" "%tempsrc%"

rem Compilation de tous les fichiers Java du répertoire tempsrc
javac -cp "lib/*" -d "." "%tempsrc%\*.java"

rem Decompresser en jar
jar -cf "lib/%Sals%.jar" -C . "%controller%"

rem Suppression du dossier controller et tempsrc
if exist "%controller%" (
    rd /S /Q "%controller%"
    echo Le dossier %controller% et son contenu ont ete supprimes avec succes.
)

if exist "%tempsrc%" (
    rd /S /Q "%tempsrc%"
    echo Le dossier %tempsrc% et son contenu ont ete supprimes avec succes.
)

rem Verifier et annoncer le succes de la creation du fichier JAR
if exist "lib/%Sals%.jar" (
    echo La librairie %Sals%.jar a ete cree avec succes.
) else (
    echo Erreur : La librairie %Sals%.jar n'a pas ete creee.
)

pause