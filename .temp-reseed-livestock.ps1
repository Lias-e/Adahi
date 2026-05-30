$ErrorActionPreference='Stop'
# Credentials are read from environment variables to avoid committing secrets.
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
# Delete existing documents (per-document deletes for reliability)
$cur=Invoke-RestMethod -Method Get -Uri "https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/documents/$col?pageSize=1000" -Headers $h -ErrorAction SilentlyContinue
if($cur.documents){ foreach($d in @($cur.documents)){ $url = "https://firestore.googleapis.com/v1/$($d.name)"; try{ Invoke-RestMethod -Method Delete -Uri $url -Headers $h -ErrorAction Stop } catch { } } }
# Prepare breeds
$sb=@(@{b='أولاد جلال';n='سلالة جزائرية أصيلة معروفة بالبنية القوية واللحم الجيد.'},@{b='حمراء الدغمة';n='سلالة محلية حمراء اللون معروفة بالتأقلم والصلابة.'},@{b='رمبي';n='سلالة سهبية قوية البنية ومناسبة للتسمين.'},@{b='بربري';n='سلالة جبلية صغيرة الحجم ومتماسكة البنية.'},@{b='دمان';n='سلالة واحية ذات أصل صحراوي وخفيفة البنية.'},@{b='صيداوي';n='سلالة صحراوية طويلة القامة ومعروفة بالصلابة.'},@{b='تازقزاوث';n='سلالة جبلية نادرة ذات مظهر محلي مميز.'},@{b='تادميت';n='سلالة سهبية متأقلمة مع البيئة المحلية.'})
$gb=@(@{b='عربية';n='سلالة محلية قوية ومناسبة للتربية التقليدية.'},@{b='قبائلي جبلي';n='سلالة جبلية قوية التكوين ومناسبة للمناطق الوعرة.'},@{b='مكاتي';n='سلالة صحراوية من الجنوب الجزائري تتأقلم جيداً.'},@{b='مزابي';n='سلالة واحية تراثية معروفة في البيئات الجافة.'},@{b='هجين جبلي';n='سلالة متأقلمة مع البيئة المحلية وبنية متوازنة.'},@{b='أوراس';n='سلالة جبلية من الشرق الجزائري معروفة بالصلابة.'},@{b='الظهرة';n='سلالة داكنة اللون مناسبة للتربية والتسمين.'},@{b='سهبية محلية';n='سلالة متأقلمة مع السهوب المفتوحة والبيع الموسمي.'})
$bb=@(@{b='بقرة حلوب';n='سلالة محلية للاستخدام الحليبي والتربية.'},@{b='بقرة لحمية';n='سلالة مخصصة للإنتاج اللحمي وقوية البنية.'},@{b='هجين ثور';n='سلالة هجينة متوازنة للتسمين والرعاية.'},@{b='بقرة جبلية';n='سلالة تتحمل الظرف الجبلي والبرد.'},@{b='بقرة صحراوية';n='سلالة متأقلمة مع البيئات القاحلة.'},@{b='سلالة محلية كبيرة';n='بقرة محلية معروفة بالقوة والحجم.'},@{b='ثور شرق';n='سلالة من الشرق معروفة بالصلابة.'},@{b='هجين حلوب';n='سلالة هجينة منتجة للحليب واللحم.'})
$ages_s=@('8 أشهر','9 أشهر','10 أشهر','11 شهر','12 شهر','13 شهر','14 شهر','15 شهر','16 شهر','18 شهر','20 شهر','2 سنوات')
$ages_g=@('12 شهر','14 شهر','15 شهر','16 شهر','18 شهر','20 شهر','2 سنوات')
$ages_b=@('1 سنة','2 سنة','3 سنة','4 سنة','5 سنة')
$w=@()
# 60 sheep: S001..S060
for($i=1;$i -le 60;$i++){
    $m=$sb[($i-1)%$sb.Count]
    $age=$ages_s[($i-1)%$ages_s.Count]
    $wgt=[Math]::Round(32.0 + (($i-1)*0.9) + (($i%4)*0.3),1)
    $price=RoundK(42000 + (($i-1)*2000) + (($i%5)*500))
    $id = ('S{0:000}' -f $i)
    $e=@{id=$id; type='خروف'; name="$($m.b) - $age"; breed=$m.b; age=$age; price=$price; weight=$wgt; quantity=1; description=$m.n; gender='ذكر'; healthStatus=($(if($i%3 -eq 0){'مفحوص'} else {'سليم'})); salesPoint='سوق المواشي'}
    $f=@{}
    foreach($k in $e.Keys){ if($k -ne 'id'){ $f[$k]=V $e[$k] } }
    $w += @{update=@{name="projects/$projectId/databases/(default)/documents/$col/$($e.id)"; fields=$f}}
}
# 20 goats: G061..G080
for($i=61;$i -le 80;$i++){
    $m=$gb[($i-61)%$gb.Count]
    $age=$ages_g[($i-61)%$ages_g.Count]
    $wgt=[Math]::Round(26.0 + (($i-61)*0.6) + (($i%3)*0.35),1)
    $price=RoundK(54000 + (($i-61)*1500) + (($i%4)*500))
    $id = ('G{0:000}' -f $i)
    $e=@{id=$id; type='عنز'; name="$($m.b) - $age"; breed=$m.b; age=$age; price=$price; weight=$wgt; quantity=1; description=$m.n; gender=($(if($i%2 -eq 0){'ذكر'} else {'أنثى'})); healthStatus=($(if($i%4 -eq 0){'مفحوص'} else {'سليم'})); salesPoint='سوق الماعز'}
    $f=@{}
    foreach($k in $e.Keys){ if($k -ne 'id'){ $f[$k]=V $e[$k] } }
    $w += @{update=@{name="projects/$projectId/databases/(default)/documents/$col/$($e.id)"; fields=$f}}
}
# 20 bulls: B081..B100
for($i=81;$i -le 100;$i++){
    $m=$bb[($i-81)%$bb.Count]
    $age=$ages_b[($i-81)%$ages_b.Count]
    $wgt=[Math]::Round(400.0 + (($i-81)*12) + (($i%5)*3.5),1)
    $price=RoundK(800000 + (($i-81)*25000) + (($i%6)*5000))
    $id = ('B{0:000}' -f $i)
    $e=@{id=$id; type='ثور'; name="$($m.b) - $age"; breed=$m.b; age=$age; price=$price; weight=$wgt; quantity=1; description=$m.n; gender='ذكر'; healthStatus=($(if($i%2 -eq 0){'مفحوص'} else {'سليم'})); salesPoint='سوق المواشي'}
    $f=@{}
    foreach($k in $e.Keys){ if($k -ne 'id'){ $f[$k]=V $e[$k] } }
    $w += @{update=@{name="projects/$projectId/databases/(default)/documents/$col/$($e.id)"; fields=$f}}
}
$body=@{writes=$w} | ConvertTo-Json -Depth 30
Invoke-RestMethod -Method Post -Uri "https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/documents:commit" -Headers $h -ContentType 'application/json' -Body $body | Out-Null
Write-Host "Seeding complete: 60 sheep + 20 goats + 20 bulls, all breed-focused descriptions."
