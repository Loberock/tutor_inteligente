$envPath = Join-Path $PSScriptRoot "..\.env"

if (Test-Path $envPath) {
    Get-Content $envPath | ForEach-Object {
        $line = $_.Trim()

        if ($line -eq "" -or $line.StartsWith("#")) {
            return
        }

        $parts = $line.Split("=", 2)

        if ($parts.Length -eq 2) {
            [Environment]::SetEnvironmentVariable($parts[0], $parts[1], "Process")
        }
    }
}

& "$PSScriptRoot\..\mvnw.cmd" spring-boot:run
