param(
    [Parameter(Mandatory = $true)][string]$KeystorePath,
    [string]$KeyAlias = 'pocket-tasks'
)
$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
$resolvedKey = (Resolve-Path -LiteralPath $KeystorePath).Path
$secureStorePassword = Read-Host 'Keystore password (hidden)' -AsSecureString
$secureKeyPassword = Read-Host 'Key password (hidden)' -AsSecureString
$oldValues = @{}
$names = @('ANDROID_KEYSTORE_PATH', 'ANDROID_KEYSTORE_PASSWORD', 'ANDROID_KEY_ALIAS', 'ANDROID_KEY_PASSWORD')
foreach ($name in $names) { $oldValues[$name] = [Environment]::GetEnvironmentVariable($name, 'Process') }
try {
    $env:ANDROID_KEYSTORE_PATH = $resolvedKey
    $env:ANDROID_KEY_ALIAS = $KeyAlias
    # Gradle needs strings. Keep them only in this process for the build duration.
    $env:ANDROID_KEYSTORE_PASSWORD = [System.Net.NetworkCredential]::new('', $secureStorePassword).Password
    $env:ANDROID_KEY_PASSWORD = [System.Net.NetworkCredential]::new('', $secureKeyPassword).Password
    Push-Location $repoRoot
    try {
        & ./gradlew.bat --no-daemon :app:testReleaseUnitTest :app:lintRelease :app:assembleRelease :app:bundleRelease
        if ($LASTEXITCODE -ne 0) { throw 'Release build failed; do not distribute old output.' }
        $distPath = Join-Path $repoRoot 'dist'
        New-Item -ItemType Directory -Force -Path $distPath | Out-Null
        Copy-Item -LiteralPath 'app/build/outputs/apk/release/app-release.apk' -Destination "$distPath/pocket-tasks.apk"
        Copy-Item -LiteralPath 'app/build/outputs/bundle/release/app-release.aab' -Destination "$distPath/pocket-tasks.aab"
        $hash = (Get-FileHash -LiteralPath "$distPath/pocket-tasks.apk" -Algorithm SHA256).Hash.ToLowerInvariant()
        "$hash  pocket-tasks.apk" | Set-Content -LiteralPath "$distPath/SHA256SUMS.txt" -Encoding ascii
        Write-Host 'Build completed. Verify APK signature and test the release on a phone before publishing.'
        Write-Host "Artifacts: $distPath"
    } finally { Pop-Location }
} finally {
    foreach ($name in $names) { [Environment]::SetEnvironmentVariable($name, $oldValues[$name], 'Process') }
}
