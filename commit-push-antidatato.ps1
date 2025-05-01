param (
    [string]$message = "Commit antidatato",
    [string]$date = (Get-Date).AddDays(-14).ToString("yyyy-MM-ddTHH:mm:ss")  # default 2 settimane fa
)

Write-Host "Eseguo git add ."
git add .

Write-Host "Commit con messaggio: $message"
Write-Host "Commit con data: $date"

# Imposta variabili d'ambiente per commit antidatato
$env:GIT_AUTHOR_DATE = $date
$env:GIT_COMMITTER_DATE = $date

git commit -m $message

# Rimuove le variabili d'ambiente
Remove-Item Env:\GIT_AUTHOR_DATE
Remove-Item Env:\GIT_COMMITTER_DATE

Write-Host "Imposto branch main e faccio push"
git branch -M main
git push -u origin main
