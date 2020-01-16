package no.acat.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import no.dcat.shared.Contact;
import no.dcat.shared.DatasetReference;
import no.dcat.shared.HarvestMetadata;
import no.dcat.shared.Publisher;
import no.acat.common.model.ApiEditableProperties;
import no.acat.common.model.apispecification.ApiSpecification;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(includeFieldNames = false, callSuper=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiDocument extends ApiEditableProperties {
    @ApiModelProperty("The id given by the harvest system")
    private String id;

    @ApiModelProperty("The source where the record was harvested from")
    private String harvestSourceUri;

    @ApiModelProperty("The url of the specification which are used to harvest the specification ")
    private String apiSpecUrl;

    @ApiModelProperty("Original API spec")
    private String apiSpec;

    @ApiModelProperty("Parsed api specification")
    private ApiSpecification apiSpecification;

    @ApiModelProperty("information about when the api was first and last harvested by the system")
    private HarvestMetadata harvest;

    @ApiModelProperty("the title of the api, can be specified in multiple langauges [dct:title]")
    private String title;

    @ApiModelProperty("the description of the api, can be specified in multiple languages [dct:description]")
    private String description;
    private String descriptionFormatted;

    @ApiModelProperty("An overview of the formats returned by the api")
    private Set<String> formats;

    @ApiModelProperty("The publisher of the api [dct:publisher]")
    private Publisher publisher;

    @ApiModelProperty("The contact point [dcat:contactPoint]")
    private List<Contact> contactPoint;

    @ApiModelProperty("A list of references to the datasets that can be returned by the api")
    private Set<DatasetReference> datasetReferences;
}
