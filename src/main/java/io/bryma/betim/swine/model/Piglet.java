package io.bryma.betim.swine.model;

import eu.smartsocietyproject.pf.AttributeType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "swine")
public class Piglet {

    @Id
    private String id;



}
