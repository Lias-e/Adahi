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
$deleted=0
foreach($prefix in @('G','B')){
    for($i=1;$i -le 200;$i++){
        $id = ('{0}{1:000}' -f $prefix,$i)
        $url = "https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/documents/$col/$id"
        try{ Invoke-RestMethod -Method Delete -Uri $url -Headers $h -ErrorAction Stop; $deleted++; Write-Host "Deleted $id" } catch { }
    }
}
Write-Host "Total deleted (G/B): $deleted"
