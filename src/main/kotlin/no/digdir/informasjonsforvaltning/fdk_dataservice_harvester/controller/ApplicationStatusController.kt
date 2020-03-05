package no.digdir.informasjonsforvaltning.fdk_dataservice_harvester.controller

import no.digdir.informasjonsforvaltning.fdk_dataservice_harvester.fuseki.Fuseki
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ApplicationStatusController(private val fuseki: Fuseki) {

    @GetMapping("/ping")
    fun ping(): ResponseEntity<Void> {
        return ResponseEntity.ok().build()
    }

    @GetMapping("/ready")
    fun ready(): ResponseEntity<Void> {
        try {
            fuseki.isReachable()
            return ResponseEntity.ok().build()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build()
    }
}