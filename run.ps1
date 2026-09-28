Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "   RentEasy - PG and Hostel Room Rent Tracker" -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Cyan

$mvnCmd = "mvn"
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    $cachedMvn = "$env:USERPROFILE\.m2\wrapper\dists\apache-maven-3.9.16\0daed3be3ebd1c706f0e69e8b07c6b73f5cc4ea3dfce72a8d0ec2e849ca2ddb0\bin\mvn.cmd"
    if (Test-Path $cachedMvn) {
        $mvnCmd = $cachedMvn
    } else {
        Write-Error "Maven not found in PATH or .m2 cache."
        exit 1
    }
}

Write-Host "Starting RentEasy Spring Boot Application..." -ForegroundColor Yellow
& $mvnCmd spring-boot:run
