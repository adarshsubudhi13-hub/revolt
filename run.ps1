$env:JAVA_HOME = "C:\Users\adars\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.12.101-hotspot"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host " Starting ReVolt Platform (Spring Boot + Pure Web)" -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host " URL: http://localhost:8085" -ForegroundColor Yellow
Write-Host " Admin:    admin@revolt.com / admin123"
Write-Host " Agent:    agent@revolt.com / agent123"
Write-Host " Customer: rohit@example.com / user123 (15,000 pts)"
Write-Host "========================================================" -ForegroundColor Cyan

mvn spring-boot:run
