package no.digdir.informasjonsforvaltning.fdk_dataservice_harvester.fuseki

import no.digdir.informasjonsforvaltning.fdk_dataservice_harvester.configuration.FusekiProperties
import org.apache.jena.query.ReadWrite
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdfconnection.RDFConnection
import org.apache.jena.rdfconnection.RDFConnectionFuseki
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

private val LOGGER = LoggerFactory.getLogger(HarvestFuseki::class.java)

@Service
class HarvestFuseki(private val fusekiProperties: FusekiProperties) {

    private fun dataServiceConnection(): RDFConnection =
        RDFConnectionFuseki.create()
            .destination(this.fusekiProperties.dataserviceUri)
            .queryEndpoint("${this.fusekiProperties.dataserviceUri}/query")
            .updateEndpoint("${this.fusekiProperties.dataserviceUri}/update")
            .build()

    fun fetchCompleteModel(): Model =
        dataServiceConnection().use {
            it.begin(ReadWrite.READ)
            return it.fetchDataset().unionModel
        }

    fun fetchByGraphName(graphName: String): Model? =
        dataServiceConnection().use {
            it.begin(ReadWrite.READ)
            return try {
                it.fetch(graphName)
            } catch (ex: Exception) {
                null
            }
        }

    fun queryDescribe(query: String): Model? =
        dataServiceConnection().use {
            it.begin(ReadWrite.READ)
            return try {
                it.queryDescribe(query)
            } catch (ex: Exception) {
                LOGGER.error("sparql exception: $ex")
                null
            }
        }

    fun saveWithGraphName(graphName: String, model: Model) =
        dataServiceConnection().use {
            it.begin(ReadWrite.WRITE)
            it.put(graphName, model)
        }

}