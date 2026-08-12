# Markdown -> PDF (Edge headless)
param(
    [Parameter(Mandatory = $true)]
    [string]$InputMd,
    [Parameter(Mandatory = $true)]
    [string]$OutputPdf
)

$ErrorActionPreference = "Stop"
$InputMd = (Resolve-Path $InputMd).Path
$OutputPdf = [System.IO.Path]::GetFullPath($OutputPdf)
$outDir = Split-Path -Parent $OutputPdf
if (-not (Test-Path $outDir)) { New-Item -ItemType Directory -Path $outDir | Out-Null }

$md = Get-Content -Path $InputMd -Raw -Encoding UTF8
$body = $md -replace '&', '&amp;' -replace '<', '&lt;' -replace '>', '&gt;'
$body = $body -replace '(?m)^### (.+)$', '<h3>$1</h3>'
$body = $body -replace '(?m)^## (.+)$', '<h2>$1</h2>'
$body = $body -replace '(?m)^# (.+)$', '<h1>$1</h1>'
$body = $body -replace '(?m)^\|(.+)\|$', { param($m)
    $cells = ($m.Groups[1].Value -split '\|') | ForEach-Object { $_.Trim() }
    if ($cells -join '' -match '^-+$') { return '' }
    $tds = ($cells | ForEach-Object { "<td>$_</td>" }) -join ''
    return "<tr>$tds</tr>"
}
$body = $body -replace '(?m)^- (.+)$', '<li>$1</li>'
$body = $body -replace '(?m)^(\d+)\. (.+)$', '<li>$2</li>'
$body = $body -replace '```powershell\r?\n([\s\S]*?)```', '<pre class="code">$1</pre>'
$body = $body -replace '```properties\r?\n([\s\S]*?)```', '<pre class="code">$1</pre>'
$body = $body -replace '```\r?\n([\s\S]*?)```', '<pre class="code">$1</pre>'
$body = $body -replace '`([^`]+)`', '<code>$1</code>'
$body = $body -replace '\*\*([^*]+)\*\*', '<strong>$1</strong>'
$body = $body -replace '(?m)^> (.+)$', '<blockquote>$1</blockquote>'
$body = $body -replace '\r?\n\r?\n', '<br/><br/>'

$html = @"
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8"/>
<title>BCJIS 배포 가이드</title>
<style>
  @page { margin: 18mm 16mm; }
  body { font-family: "Malgun Gothic", "Apple SD Gothic Neo", sans-serif; font-size: 11pt; line-height: 1.55; color: #222; }
  h1 { font-size: 20pt; border-bottom: 2px solid #1a5fb4; padding-bottom: 6px; }
  h2 { font-size: 14pt; margin-top: 1.2em; color: #1a5fb4; }
  h3 { font-size: 12pt; margin-top: 1em; }
  table { border-collapse: collapse; width: 100%; margin: 8px 0 14px; font-size: 10pt; }
  td, th { border: 1px solid #ccc; padding: 5px 8px; vertical-align: top; }
  tr:first-child td { background: #eef4fb; font-weight: bold; }
  pre.code { background: #f5f5f5; border: 1px solid #ddd; padding: 10px; font-size: 9pt; white-space: pre-wrap; word-break: break-all; }
  code { background: #f0f0f0; padding: 1px 4px; font-size: 9.5pt; }
  blockquote { border-left: 4px solid #1a5fb4; margin: 8px 0; padding: 4px 12px; background: #f8fafc; }
  li { margin: 2px 0; }
</style>
</head>
<body>
$body
</body>
</html>
"@

if (Test-Path $OutputPdf) { Remove-Item $OutputPdf -Force }

$htmlPath = Join-Path $outDir ("_pdf_" + [System.IO.Path]::GetFileNameWithoutExtension($OutputPdf) + ".html")
$utf8NoBom = New-Object System.Text.UTF8Encoding $false
[System.IO.File]::WriteAllText($htmlPath, $html, $utf8NoBom)

$edgePaths = @(
    "${env:ProgramFiles}\Microsoft\Edge\Application\msedge.exe",
    "${env:ProgramFiles(x86)}\Microsoft\Edge\Application\msedge.exe"
)
$edge = $edgePaths | Where-Object { Test-Path $_ } | Select-Object -First 1
if (-not $edge) { Write-Error "Microsoft Edge not found" }

& $edge --headless=new --disable-gpu --no-pdf-header-footer --print-to-pdf="$OutputPdf" $htmlPath 2>$null | Out-Null
Start-Sleep -Seconds 6

if (-not (Test-Path $OutputPdf)) {
    Write-Error "PDF generation failed: $OutputPdf"
}
Write-Host "PDF created: $OutputPdf"
