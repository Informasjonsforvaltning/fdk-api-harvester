package no.acat.bindings;

import lombok.Data;
import no.acat.common.model.apispecification.ApiSpecification;

import java.util.List;

@Data
public class ConvertResponse {

    ApiSpecification apiSpecification;

    List<String> messages;
}
