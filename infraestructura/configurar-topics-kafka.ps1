$ErrorActionPreference = "Stop"

$compose = Join-Path $PSScriptRoot "compose.yaml"

$topics = @(
    @{ Nombre = "pedidos.eventos"; Retencion = 604800000 },
    @{ Nombre = "pedidos.notificaciones"; Retencion = 259200000 }
)

foreach ($topic in $topics) {

    $nombre = $topic.Nombre
    $retencion = $topic.Retencion

    Write-Host "Configurando topic: $nombre"

    docker compose -f $compose exec -T kafka1 kafka-topics `
        --bootstrap-server kafka1:29092 `
        --create --if-not-exists `
        --topic $nombre `
        --partitions 3 `
        --replication-factor 3 `
        --config min.insync.replicas=2

    if ($LASTEXITCODE -ne 0) {
        throw "Error al crear o verificar el topic $nombre"
    }

    docker compose -f $compose exec -T kafka1 kafka-configs `
        --bootstrap-server kafka1:29092 `
        --entity-type topics `
        --entity-name $nombre `
        --alter `
        --add-config "retention.ms=$retencion,cleanup.policy=delete,min.insync.replicas=2"

    if ($LASTEXITCODE -ne 0) {
        throw "Error al configurar el topic $nombre"
    }

    Write-Host "Topic $nombre configurado correctamente"
}

Write-Host "CONFIGURACION KAFKA COMPLETADA"