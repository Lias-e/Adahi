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
$pageToken = $null
$deleted=0
do{
    $url = "https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/documents/$col?pageSize=100"
    if($pageToken){ $url += "&pageToken=$pageToken" }
    $res = Invoke-RestMethod -Method Get -Uri $url -Headers $h -ErrorAction SilentlyContinue
    if(-not $res.documents){ break }
    foreach($d in $res.documents){
        $type = $null
        if($d.fields.type){ $type = $d.fields.type.stringValue }
        if($type -eq 'عنز' -or $type -eq 'ثور'){
            Write-Host "Deleting by type ($type): $($d.name)"
            try{ Invoke-RestMethod -Method Delete -Uri "https://firestore.googleapis.com/v1/$($d.name)" -Headers $h -ErrorAction Stop; $deleted++ } catch { Write-Host "Failed: $($d.name)" }
        }
    }
    $pageToken = $res.nextPageToken
} while($pageToken)
Write-Host "Deleted total by type: $deleted"
