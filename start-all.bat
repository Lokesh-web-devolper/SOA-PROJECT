@echo off
title ParkGrid Solutions - System Launcher
echo ==============================================================================
echo               PARKGRID SOLUTIONS - MICROSERVICES & FRONTEND LAUNCHER
echo ==============================================================================
echo.

cd /d "%~dp0"

echo [1/7] Starting Eureka Service Registry (Port 8761)...
start "ParkGrid - Eureka Registry [8761]" cmd /k "cd backend\eureka-server && mvn spring-boot:run"
echo Waiting 15 seconds for Eureka Registry to initialize...
timeout /t 15 /nobreak >nul

echo [2/7] Starting API Gateway (Port 8080)...
start "ParkGrid - API Gateway [8080]" cmd /k "cd backend\api-gateway && mvn spring-boot:run"
echo Waiting 5 seconds...
timeout /t 5 /nobreak >nul

echo [3/7] Starting Auth Service (Port 8081)...
start "ParkGrid - Auth Service [8081]" cmd /k "cd backend\auth-service && mvn spring-boot:run"
echo Waiting 5 seconds...
timeout /t 5 /nobreak >nul

echo [4/7] Starting Parking Service (Port 8082)...
start "ParkGrid - Parking Service [8082]" cmd /k "cd backend\parking-service && mvn spring-boot:run"
echo Waiting 5 seconds...
timeout /t 5 /nobreak >nul

echo [5/7] Starting Booking Service (Port 8083)...
start "ParkGrid - Booking Service [8083]" cmd /k "cd backend\booking-service && mvn spring-boot:run"
echo Waiting 5 seconds...
timeout /t 5 /nobreak >nul

echo [6/7] Starting Billing Service (Port 8085)...
start "ParkGrid - Billing Service [8085]" cmd /k "cd backend\billing-service && mvn spring-boot:run"
echo Waiting 5 seconds...
timeout /t 5 /nobreak >nul

echo [7/7] Starting React Frontend Client (Port 5173)...
start "ParkGrid - Frontend React Client [5173]" cmd /k "cd frontend && npm run dev"

echo.
echo ==============================================================================
echo ALL PARKGRID SERVICES HAVE BEEN LAUNCHED!
echo.
echo   * React Web UI:       http://localhost:5173
echo   * API Gateway:        http://localhost:8080
echo   * Eureka Dashboard:   http://localhost:8761
echo   * Swagger UI:         http://localhost:8080/swagger-ui.html
echo.
echo Default Credentials:
echo   - Administrator:  admin@parkgrid.com  / Admin@123
echo   - Customer Driver: customer@parkgrid.com / Customer@123
echo ==============================================================================
pause
