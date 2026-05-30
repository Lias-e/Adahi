$ErrorActionPreference='Stop'
# Credentials from environment
$rt = $env:GOOGLE_REFRESH_TOKEN
$cid = $env:GOOGLE_CLIENT_ID
$cs = $env:GOOGLE_CLIENT_SECRET
$projectId = $env:FIRESTORE_PROJECT_ID
if(-not $rt -or -not $cid -or -not $cs -or -not $projectId){ Write-Error 'Set environment variables: GOOGLE_REFRESH_TOKEN, GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET, FIRESTORE_PROJECT_ID'; exit 1 }
$token=Invoke-RestMethod -Method Post -Uri 'https://oauth2.googleapis.com/token' -ContentType 'application/x-www-form-urlencoded' -Body @{client_id=$cid;client_secret=$cs;refresh_token=$rt;grant_type='refresh_token'}
$h=@{Authorization="Bearer $($token.access_token)"}
$docs=Invoke-RestMethod -Method Get -Uri "https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/documents/animals?pageSize=1000" -Headers $h
"Document count returned: $(@($docs.documents).Count)"
$docs.documents | Select-Object -First 5 | ForEach-Object { $f=$_.fields; "$($_.name) | name=$($f.name.stringValue) | type=$($f.type.stringValue) | price=$($f.price.integerValue) | desc=$($f.description.stringValue)" }
