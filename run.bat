@echo off
echo Compiling Inventory Management System...
javac -d out -cp "mysql-connector-j-8.3.0.jar" src\db\DBConnection.java src\model\*.java src\dao\*.java src\ui\*.java src\app\InventoryApp.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b
)

echo Starting Inventory Management System...
java -cp "out;mysql-connector-j-8.3.0.jar" app.InventoryApp
pause
