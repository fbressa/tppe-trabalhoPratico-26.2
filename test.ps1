# Equivalente Windows do test.sh: compila main e test (Java 17) e roda todos os *Test com -ea.
# Uso: .\test.ps1            (todos os testes)
#      .\test.ps1 ContratoApiTest   (só uma classe)
param([string[]]$Classes)
$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

if (-not (Get-ChildItem libs\*.jar -ErrorAction SilentlyContinue)) {
    Write-Error 'Coloque os .jar em libs\ (veja libs\LEIAME.txt)'
}

if (Test-Path out) { Remove-Item -Recurse -Force out }
$main = Get-ChildItem -Recurse src\main\java -Filter *.java | ForEach-Object FullName
$test = Get-ChildItem -Recurse src\test\java -Filter *.java | ForEach-Object FullName

javac --release 17 -encoding UTF-8 -d out\main $main
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
javac --release 17 -encoding UTF-8 -d out\test -cp "out\main;libs\*" $test
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Write-Host 'build OK'

if (-not $Classes) {
    $raiz = (Resolve-Path src\test\java).Path
    $Classes = Get-ChildItem -Recurse src\test\java -Filter *Test.java | ForEach-Object {
        $_.FullName.Substring($raiz.Length + 1) -replace '\.java$', '' -replace '[\\/]', '.'
    }
} else {
    $Classes = $Classes | ForEach-Object { if ($_ -like '*.*') { $_ } else { "supercompras.$_" } }
}

java -ea "-Dfile.encoding=UTF-8" -cp "out\main;out\test;libs\*" org.junit.runner.JUnitCore $Classes
exit $LASTEXITCODE
