# ParkGrid Solutions - System Launcher (PowerShell)
Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host "             PARKGRID SOLUTIONS - ENTERPRISE MICROSERVICES LAUNCHER" -ForegroundColor Cyan
Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host ""

$baseDir = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "[1/7] Launching Netflix Eureka Discovery Server (Port 8761)..." -ForegroundColor Yellow
Start-Process cmd -ArgumentList "/k title Eureka Registry [8761] && cd /d `"$baseDir\backend\eureka-server`" && mvn spring-boot:run"
Start-Sleep -Seconds 15

Write-Host "[2/7] Launching Spring Cloud API Gateway (Port 8080)..." -ForegroundColor Yellow
Start-Process cmd -ArgumentList "/k title API Gateway [8080] && cd /d `"$baseDir\backend\api-gateway`" && mvn spring-boot:run"
Start-Sleep -Seconds 5

Write-Host "[3/7] Launching Identity & Authentication Service (Port 8081)..." -ForegroundColor Yellow
Start-Process cmd -ArgumentList "/k title Auth Service [8081] && cd /d `"$baseDir\backend\auth-service`" && mvn spring-boot:run"
Start-Sleep -Seconds 5

Write-Host "[4/7] Launching Parking & Facility Service (Port 8082)..." -ForegroundColor Yellow
Start-Process cmd -ArgumentList "/k title Parking Service [8082] && cd /d `"$baseDir\backend\parking-service`" && mvn spring-boot:run"
Start-Sleep -Seconds 5

Write-Host "[5/7] Launching Concurrency-Safe Booking Service (Port 8083)..." -ForegroundColor Yellow
Start-Process cmd -ArgumentList "/k title Booking Service [8083] && cd /d `"$baseDir\backend\booking-service`" && mvn spring-boot:run"
Start-Sleep -Seconds 5

Write-Host "[6/7] Launching Dynamic Billing & Invoicing Service (Port 8085)..." -ForegroundColor Yellow
Start-Process cmd -ArgumentList "/k title Billing Service [8085] && cd /d `"$baseDir\backend\billing-service`" && mvn spring-boot:run"
Start-Sleep -Seconds 5

Write-Host "[7/7] Launching React Client Application (Port 5173)..." -ForegroundColor Yellow
Start-Process cmd -ArgumentList "/k title React Client [5173] && cd /d `"$baseDir\frontend`" && npm run dev"

Write-Host ""
Write-Host "==============================================================================" -ForegroundColor Green
Write-Host "ALL SERVICES DISPATCHED!" -ForegroundColor Green
Write-Host "  * React Dashboard:   http://localhost:5173" -ForegroundColor Green
Write-Host "  * API Gateway:      http://localhost:8080" -ForegroundColor Green
Write-Host "  * Eureka Console:   http://localhost:8761" -ForegroundColor Green
Write-Host "Default Logins:" -ForegroundColor White
Write-Host "  - Admin:    admin@parkgrid.com    / Admin@123" -ForegroundColor White
Write-Host "  - Customer: customer@parkgrid.com / Customer@123" -ForegroundColor White
Write-Host "==============================================================================" -ForegroundColor Green
