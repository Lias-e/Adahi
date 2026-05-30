$ErrorActionPreference='Stop'
# Credentials from environment
$rt = $env:GOOGLE_REFRESH_TOKEN
$cid = $env:GOOGLE_CLIENT_ID
$cs = $env:GOOGLE_CLIENT_SECRET
$projectId = $env:FIRESTORE_PROJECT_ID
$col='animals'
if(-not $rt -or -not $cid -or -not $cs -or -not $projectId){ Write-Error 'Set environment variables: GOOGLE_REFRESH_TOKEN, GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET, FIRESTORE_PROJECT_ID'; exit 1 }
function V([object]$x){ if($null -eq $x){@{nullValue=$null}} elseif($x -is [string]){@{stringValue=$x}} elseif($x -is [bool]){@{booleanValue=$x}} elseif($x -is [int] -or $x -is [long] -or $x -is [short] -or $x -is [byte]){@{integerValue="$x"}} elseif($x -is [double] -or $x -is [float] -or $x -is [decimal]){@{doubleValue=[double]$x}} else {@{stringValue=[string]$x}} }
function RoundK([double]$n){ [int]([Math]::Round($n/1000.0,0)*1000) }
$tk=Invoke-RestMethod -Method Post -Uri 'https://oauth2.googleapis.com/token' -ContentType 'application/x-www-form-urlencoded' -Body @{client_id=$cid;client_secret=$cs;refresh_token=$rt;grant_type='refresh_token'}
$h=@{Authorization="Bearer $($tk.access_token)"}
$gb=@(@{b='عربية';n='سلالة محلية قوية ومناسبة للتربية التقليدية.'},@{b='قبائلي جبلي';n='سلالة جبلية قوية التكوين ومناسبة للمناطق الوعرة.'},@{b='مكاتي';n='سلالة صحراوية من الجنوب الجزائري تتأقلم جيداً.'},@{b='مزابي';n='سلالة واحية تراثية معروفة في البيئات الجافة.'},@{b='هجين جبلي';n='سلالة متأقلمة مع البيئة المحلية وبنية متوازنة.'},@{b='أوراس';n='سلالة جبلية من الشرق الجزائري معروفة بالصلابة.'},@{b='الظهرة';n='سلالة داكنة اللون مناسبة للتربية والتسمين.'},@{b='سهبية محلية';n='سلالة متأقلمة مع السهوب المفتوحة والبيع الموسمي.'})
$bb=@(@{b='بقرة حلوب';n='سلالة محلية للاستخدام الحليبي والتربية.'},@{b='بقرة لحمية';n='سلالة مخصصة للإنتاج اللحمي وقوية البنية.'},@{b='هجين ثور';n='سلالة هجينة متوازنة للتسمين والرعاية.'},@{b='بقرة جبلية';n='سلالة تتحمل الظرف الجبلي والبرد.'},@{b='بقرة صحراوية';n='سلالة متأقلمة مع البيئات القاحلة.'},@{b='سلالة محلية كبيرة';n='بقرة محلية معروفة بالقوة والحجم.'},@{b='ثور شرق';n='سلالة من الشرق معروفة بالصلابة.'},@{b='هجين حلوب';n='سلالة هجينة منتجة للحليب واللحم.'})
$w=@()
# Goats G061..G080
for($i=61;$i -le 80;$i++){
    $m=$gb[($i-61)%$gb.Count]
    $age='14 شهر'
    $wgt=[Math]::Round(26.0 + (($i-61)*0.6),1)
    $price=RoundK(56000 + (($i-61)*1500))
    $id = ('G{0:000}' -f $i)
    $e=@{id=$id; type='عنز'; name="$($m.b) - $age"; breed=$m.b; age=$age; price=$price; weight=$wgt; quantity=1; description=$m.n; gender=($(if($i%2 -eq 0){'ذكر'} else {'أنثى'})); healthStatus='سليم'; salesPoint='سوق الماعز'}
    $f=@{}
    foreach($k in $e.Keys){ if($k -ne 'id'){ $f[$k]=V $e[$k] } }
    $w += @{update=@{name="projects/$projectId/databases/(default)/documents/$col/$($e.id)"; fields=$f}}
}
# Bulls B081..B100
for($i=81;$i -le 100;$i++){
    $m=$bb[($i-81)%$bb.Count]
    $age='3 سنة'
    $wgt=[Math]::Round(450.0 + (($i-81)*10),1)
    $price=RoundK(820000 + (($i-81)*20000))
    $id = ('B{0:000}' -f $i)
    $e=@{id=$id; type='ثور'; name="$($m.b) - $age"; breed=$m.b; age=$age; price=$price; weight=$wgt; quantity=1; description=$m.n; gender='ذكر'; healthStatus='سليم'; salesPoint='سوق المواشي'}
    $f=@{}
    foreach($k in $e.Keys){ if($k -ne 'id'){ $f[$k]=V $e[$k] } }
    $w += @{update=@{name="projects/$projectId/databases/(default)/documents/$col/$($e.id)"; fields=$f}}
}
$body=@{writes=$w} | ConvertTo-Json -Depth 30
Invoke-RestMethod -Method Post -Uri "https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/documents:commit" -Headers $h -ContentType 'application/json' -Body $body | Out-Null
Write-Host "Added goats G061..G080 and bulls B081..B100"
