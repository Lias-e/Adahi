$ErrorActionPreference='Stop'
# Credentials from environment
$rt = $env:GOOGLE_REFRESH_TOKEN
$cid = $env:GOOGLE_CLIENT_ID
$cs = $env:GOOGLE_CLIENT_SECRET
$projectId = $env:FIRESTORE_PROJECT_ID
$col='animals'
if(-not $rt -or -not $cid -or -not $cs -or -not $projectId){ Write-Error 'Set environment variables: GOOGLE_REFRESH_TOKEN, GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET, FIRESTORE_PROJECT_ID'; exit 1 }
$token=Invoke-RestMethod -Method Post -Uri 'https://oauth2.googleapis.com/token' -ContentType 'application/x-www-form-urlencoded' -Body @{client_id=$cid;client_secret=$cs;refresh_token=$rt;grant_type='refresh_token'}
$h=@{Authorization="Bearer $($token.access_token)"}
while($true){
    $cur=Invoke-RestMethod -Method Get -Uri "https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/documents/$col?pageSize=1000" -Headers $h -ErrorAction SilentlyContinue
    if(-not $cur.documents){ Write-Host "No documents left."; break }
    $count=0
    foreach($d in @($cur.documents)){ $url="https://firestore.googleapis.com/v1/$($d.name)"; try{ Invoke-RestMethod -Method Delete -Uri $url -Headers $h -ErrorAction Stop; $count++ } catch { }
    }
    Write-Host "Deleted batch: $count"
    Start-Sleep -Seconds 1
}
