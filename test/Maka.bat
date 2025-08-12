@echo off
setlocal enabledelayedexpansion

rem === Definition des chemins ===
set "destination=lib"
set "source=J:\Perso\Work\I.T.University\Licence\L2\S4\WEB DYNAMIQUE (Mr Naina)\SPRINT_S4\lib"

rem === Suppression du dossier de destination s'il existe ===
if exist "%destination%" (
    rd /S /Q "%destination%"
    echo [INFO] Le dossier %destination% et son contenu ont ete supprimes avec succès.
)

rem === Creation du nouveau dossier de destination ===
mkdir "%destination%"
echo [INFO] Le dossier %destination% a ete cree avec succès.

rem === Copie des fichiers JAR ===
set "errorOccurred=false"
for /r "%source%" %%f in (*.jar) do (
    copy "%%f" "%destination%\" >nul
    if !errorlevel! neq 0 (
        echo [ERREUR] echec de la copie du fichier : %%f
        set "errorOccurred=true"
    )
)

rem === Verification de la reussite de la copie ===
if "!errorOccurred!" == "true" (
    echo [ERREUR] Certaines copies de fichiers ont echoue.
    pause
    exit /b 1
)

echo [SUCCÈS] Copie effectuee avec succès.

pause
