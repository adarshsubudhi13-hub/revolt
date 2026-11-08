@echo off
set "JAVA_HOME=C:\Users\adars\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.12.101-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo ========================================================
echo  Starting ReVolt Platform (Spring Boot + Pure Web)
echo ========================================================
echo  URL: http://localhost:8085
echo  Admin:    admin@revolt.com / admin123
echo  Agent:    agent@revolt.com / agent123
echo  Customer: rohit@example.com / user123
echo ========================================================

mvn spring-boot:run
pause
