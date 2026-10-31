@echo off
title ParkGrid Solutions - System Shutdown
echo Stopping all ParkGrid Solutions microservices and frontend servers...
taskkill /f /im java.exe 2>nul
taskkill /f /im node.exe 2>nul
echo All services stopped.
pause
