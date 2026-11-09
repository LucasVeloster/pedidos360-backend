$ErrorActionPreference = "Stop"

$compose = Join-Path $PSScriptRoot "compose.yaml"

Write-Host "Iniciando RabbitMQ..."

docker compose -f $compose up -d rabbitmq1 rabbitmq2

if ($LASTEXITCODE -ne 0) {
    throw "No se pudieron iniciar los contenedores RabbitMQ."
}

Write-Host "Comprobando disponibilidad de los nodos..."

foreach ($nodo in @("rabbitmq1", "rabbitmq2")) {
    $listo = $false

    for ($i = 1; $i -le 30; $i++) {
        docker compose -f $compose exec -T $nodo rabbitmq-diagnostics -q check_running 2>$null | Out-Null

        if ($LASTEXITCODE -eq 0) {
            $listo = $true
            break
        }

        Start-Sleep -Seconds 2
    }

    if (-not $listo) {
        throw "El nodo $nodo no inicio correctamente."
    }
}

$estado = docker compose -f $compose exec -T rabbitmq1 rabbitmqctl cluster_status

if ($LASTEXITCODE -ne 0) {
    throw "No se pudo consultar el estado del cluster."
}

$seccion = ($estado -join "`n") -split "Running Nodes", 2

if ($seccion.Count -gt 1 -and
    $seccion[1] -match "rabbit@rabbitmq2") {

    Write-Host "Los nodos ya estan unidos."
}
else {
    Write-Host "Uniendo rabbitmq2 al cluster..."

    docker compose -f $compose exec -T rabbitmq2 rabbitmqctl join_cluster rabbit@rabbitmq1

    if ($LASTEXITCODE -ne 0) {
        throw "No se pudo unir rabbitmq2 al cluster."
    }
}

Write-Host "Estado final del cluster:"

docker compose -f $compose exec -T rabbitmq1 rabbitmqctl cluster_status