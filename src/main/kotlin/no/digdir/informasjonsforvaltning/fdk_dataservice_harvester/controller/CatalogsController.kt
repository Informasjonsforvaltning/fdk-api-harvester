package no.digdir.informasjonsforvaltning.fdk_dataservice_harvester.controller

import no.digdir.informasjonsforvaltning.fdk_dataservice_harvester.generated.api.DcatApNoCatalogsApi
import no.digdir.informasjonsforvaltning.fdk_dataservice_harvester.rdf.returnTypeFromAcceptHeader
import no.digdir.informasjonsforvaltning.fdk_dataservice_harvester.service.CatalogService
import no.digdir.informasjonsforvaltning.fdk_dataservice_harvester.service.DataServiceService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import javax.servlet.http.HttpServletRequest

private val LOGGER = LoggerFactory.getLogger(DataserviceController::class.java)

@Controller
open class DataserviceController(private val catalogService: CatalogService) : DcatApNoCatalogsApi {

    override fun getCatalogById(httpServletRequest: HttpServletRequest, id: String): ResponseEntity<String> {
        LOGGER.info("get DataService catalog with id $id")
        val returnType = returnTypeFromAcceptHeader(httpServletRequest.getHeader("Accept"))

        return catalogService.getDataServiceCatalog(id, returnType)
            ?.let { ResponseEntity(it, HttpStatus.OK) }
            ?: ResponseEntity.notFound().build()
    }

    override fun getCatalogs(httpServletRequest: HttpServletRequest): ResponseEntity<String> {
        LOGGER.info("get all DataService catalogs")
        val returnType = returnTypeFromAcceptHeader(httpServletRequest.getHeader("Accept"))
        return ResponseEntity(catalogService.getAllDataServiceCatalogs(returnType), HttpStatus.OK)
    }
}