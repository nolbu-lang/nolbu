# LLM Studio budget_search API test (submit to internal AI team)
# Usage: .\scripts\test-llm-studio-budget-search.ps1
#        .\scripts\test-llm-studio-budget-search.ps1 -UserQuery "your question"

param(
    [string]$UserQuery = [string]::Concat(
        [char]0xBD80, [char]0xC0B0, [char]0xC2DC, [char]0xCCAD, ' AI ',
        [char]0xC608, [char]0xC0B0, ' ',
        [char]0xC2EC, [char]0xC758, [char]0xBC29, [char]0xBC95, [char]0xC744, ' ',
        [char]0xC54C, [char]0xB824, [char]0xC918
    )
)

$endpoint = 'http://99.1.82.207:8080/llm-studio/v1/api/task/generate/syncapi/busan_ai_llm/budget_search'
$culture = [System.Globalization.CultureInfo]::GetCultureInfo('ko-KR')
$datetime = (Get-Date).ToString('yyyy년 M월 d일 dddd tt h:mm', $culture)

$bodyObj = @{
    user_query     = $UserQuery
    datetime       = $datetime
    stream         = $true
    dialog_history = '[]'
}
$body = $bodyObj | ConvertTo-Json -Compress

Write-Host "POST $endpoint"
Write-Host "Request body:"
Write-Host $body
Write-Host ''

try {
    $response = Invoke-RestMethod -Uri $endpoint -Method Post -Body $body -ContentType 'application/json; charset=utf-8'
    Write-Host 'HTTP 200 OK'
    Write-Host 'Response:'
    $response | ConvertTo-Json -Depth 10
    if ($response.llm_result.answer) {
        Write-Host ''
        Write-Host '=== answer ==='
        Write-Host $response.llm_result.answer
    }
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.ErrorDetails.Message) {
        Write-Host $_.ErrorDetails.Message
    }
    exit 1
}
