package no.acat.harvester;

import no.acat.model.ApiDocument;
import no.acat.repository.ApiDocumentRepository;
import no.acat.service.ApiDocumentBuilderService;
import no.acat.service.RegistrationApiClient;
import no.fdk.test.testcategories.UnitTest;
import no.fdk.acat.common.model.ApiRegistrationPublic;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@Category(UnitTest.class)
@RunWith(SpringRunner.class)
public class ApiHarvestTest {

    private ApiDocumentBuilderService apiDocumentBuilderServiceMock;
    private ApiDocumentRepository apiDocumentRepositoryMock;

    @Before
    public void setup() throws Throwable {
        apiDocumentRepositoryMock = mock(ApiDocumentRepository.class);

        apiDocumentBuilderServiceMock = mock(ApiDocumentBuilderService.class);
        when(apiDocumentBuilderServiceMock.createFromApiRegistration(any(), any(), any())).thenReturn(new ApiDocument());
    }

    @Test
    public void harvestAllOK() throws Throwable {

        List<ApiRegistrationPublic> publishedApis = new ArrayList<>();
        publishedApis.add(new ApiRegistrationPublic());

        RegistrationApiClient registrationApiClientMock = mock(RegistrationApiClient.class);
        when(registrationApiClientMock.getPublished()).thenReturn(publishedApis);

        ApiHarvester harvester = new ApiHarvester(apiDocumentBuilderServiceMock, registrationApiClientMock, apiDocumentRepositoryMock);

        harvester.harvestAll();

        verify(apiDocumentRepositoryMock, times(25)).createOrReplaceApiDocument(any());
    }


    @Test
    public void harvestAllShouldWorkWithEmtpyRegistration() throws Throwable {

        RegistrationApiClient registrationApiClientMock = mock(RegistrationApiClient.class);
        when(registrationApiClientMock.getPublished()).thenReturn(new ArrayList<>());


        ApiHarvester harvester = new ApiHarvester(apiDocumentBuilderServiceMock, registrationApiClientMock, apiDocumentRepositoryMock);

        ApiHarvester harvesterSpy = spy(harvester);

        doReturn(new ArrayList<>()).when(harvesterSpy).getApiRegistrationsFromCsv();

        harvesterSpy.harvestAll();

        verify(apiDocumentRepositoryMock, times(0)).createOrReplaceApiDocument(any());
    }

    @Test
    public void harvestAllShouldWorkWithEmtpyPublished() throws Throwable {

        RegistrationApiClient registrationApiClientMock = mock(RegistrationApiClient.class);
        when(registrationApiClientMock.getPublished()).thenReturn(new ArrayList<>());

        ApiHarvester harvester = new ApiHarvester(apiDocumentBuilderServiceMock, registrationApiClientMock, apiDocumentRepositoryMock);

        harvester.harvestAll();

        verify(apiDocumentRepositoryMock, times(24)).createOrReplaceApiDocument(any());
    }

    @Test(expected = RuntimeException.class)
    public void shouldGetExceptionWhenHarvestApiFails() {
        RegistrationApiClient registrationApiClientMock = mock(RegistrationApiClient.class);
        when(registrationApiClientMock.getPublished()).thenReturn(null);


        ApiHarvester harvester = new ApiHarvester(apiDocumentBuilderServiceMock, registrationApiClientMock, apiDocumentRepositoryMock);
        harvester.RETRY_COUNT_API_RETRIEVAL = 5;
        harvester.harvestAll();//Throws exception.
    }
}
