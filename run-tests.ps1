$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $MyInvocation.MyCommand.Definition
$libs = Join-Path $root 'libs'
$targetClasses = Join-Path $root 'target\classes'
$targetTest = Join-Path $root 'target\test-classes'

function Ensure-Jar {
    param([string]$name, [string]$url)
    if (-not (Test-Path $libs)) { New-Item -ItemType Directory -Path $libs | Out-Null }
    $dest = Join-Path $libs $name
    if (-not (Test-Path $dest)) {
        Write-Host "Downloading $name..."
        Invoke-WebRequest -Uri $url -OutFile $dest
    }
    return $dest
}

# Ensure junit/hamcrest jars are present
$junitJar = Ensure-Jar -name 'junit-4.13.2.jar' -url 'https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar'
$hamcrestJar = Ensure-Jar -name 'hamcrest-core-1.3.jar' -url 'https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar'

# Build main classes
& "$root\compile.bat"

# Prepare output folders
if (-not (Test-Path $targetTest)) { New-Item -ItemType Directory -Path $targetTest | Out-Null }

# Compile tests
$classpath = "$junitJar;$hamcrestJar;$targetClasses"
Write-Host "Compiling tests..."
& javac -cp $classpath -d $targetTest "$root\src\test\java\main\*.java"

# Run tests
$runClasspath = "$junitJar;$hamcrestJar;$targetClasses;$targetTest"
Write-Host "Running tests..."
& java -cp $runClasspath org.junit.runner.JUnitCore main.ModelCostsResetTest main.ModelPlacementRulesTest
