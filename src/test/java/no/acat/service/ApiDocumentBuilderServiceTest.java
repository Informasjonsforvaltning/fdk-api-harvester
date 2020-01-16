package no.acat.service;

import no.acat.model.ApiDocument;
import no.acat.repository.ApiDocumentRepository;
import no.acat.utils.Utils;
import no.fdk.test.testcategories.UnitTest;
import no.acat.converters.apispecificationparser.ParseException;
import no.acat.common.model.ApiRegistrationPublic;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ApiDocumentBuilderServiceTest {

    ApiDocumentBuilderService apiDocumentBuilderService;

    ApiDocumentRepository apiDocumentRepositoryMock;
    PublisherCatClient publisherCatClientMock;
    DatasetCatClient datasetCatClientMock;

    @Before
    public void setup() {

        apiDocumentRepositoryMock = mock(ApiDocumentRepository.class);
        publisherCatClientMock = mock(PublisherCatClient.class);
        datasetCatClientMock = mock(DatasetCatClient.class);

        apiDocumentBuilderService = new ApiDocumentBuilderService(apiDocumentRepositoryMock, publisherCatClientMock, datasetCatClientMock);
    }

    @Test
    public void checkIfApiDocumentIsCreated() throws IOException, ParseException {
        String spec = Utils.getStringFromResource("enhetsregisteret-openapi3.json");
        ApiRegistrationPublic apiRegistrationPublic = new ApiRegistrationPublic();
        apiRegistrationPublic.setApiSpec(spec);
        String harvestSourceUri = "x";
        Date harvestDate = new Date();

        ApiDocument apiDocument =
            apiDocumentBuilderService.createFromApiRegistration(apiRegistrationPublic, harvestSourceUri, harvestDate);
        assertEquals(apiDocument.getHarvestSourceUri(), harvestSourceUri);
        assertEquals(apiDocument.getTitle(), "Åpne Data fra Enhetsregisteret - API Dokumentasjon");
    }

    @Test
    public void checkIfExistingApiDocumentIsUpdated() throws IOException, ParseException {
        String spec = Utils.getStringFromResource("enhetsregisteret-openapi3.json");
        ApiRegistrationPublic apiRegistrationPublic = new ApiRegistrationPublic();
        apiRegistrationPublic.setApiSpec(spec);
        String harvestSourceUri = "x";
        Date harvestDate = new Date();
        ApiDocument oldApiDocument =
            apiDocumentBuilderService.createFromApiRegistration(apiRegistrationPublic, harvestSourceUri, harvestDate);

        when(apiDocumentRepositoryMock.getApiDocumentByHarvestSourceUri(harvestSourceUri)).thenReturn(Optional.of(oldApiDocument));

        Date secondHarvestDate = new Date();
        secondHarvestDate.setTime(harvestDate.getTime() + 1000);
        apiRegistrationPublic.setCost("+1");

        ApiDocument apiDocument =
            apiDocumentBuilderService.createFromApiRegistration(apiRegistrationPublic, harvestSourceUri, secondHarvestDate);

        assertEquals(apiDocument.getId(), oldApiDocument.getId());
        assertEquals(apiDocument.getHarvestSourceUri(), harvestSourceUri);
        assertEquals(apiDocument.getCost(), apiRegistrationPublic.getCost());
        assertEquals(apiDocument.getHarvest().getChanged().get(0), secondHarvestDate);
        assertEquals(apiDocument.getHarvest().getFirstHarvested(), harvestDate);
        assertEquals(apiDocument.getHarvest().getLastChanged(), secondHarvestDate);
        assertEquals(apiDocument.getHarvest().getLastHarvested(), secondHarvestDate);
    }

}
